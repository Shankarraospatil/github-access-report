package com.cloudeagle.githubaccessreport.service;

import com.cloudeagle.githubaccessreport.model.AccessReport;
import com.cloudeagle.githubaccessreport.model.Repository;
import com.cloudeagle.githubaccessreport.model.RepositoryAccess;
import com.cloudeagle.githubaccessreport.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GithubService {

    private final WebClient webClient;

    @Value("${github.token}")
    private String token;

    public GithubService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.github.com").build();
    }

    public AccessReport generateReport(String org) {
        List<String> repos = getRepos(org);
        List<Repository> repositories = repos.stream()
                .map(repo -> getCollaboratorsMono(org, repo)
                        .map(users -> new Repository(repo, users))
                        .block())
                .collect(Collectors.toList());

        Map<String, List<RepositoryAccess>> userToRepos = new HashMap<>();
        for (Repository repo : repositories) {
            for (User user : repo.getCollaborators()) {
                userToRepos.computeIfAbsent(user.getLogin(), k -> new ArrayList<>())
                        .add(new RepositoryAccess(repo.getName(), user.getRole()));
            }
        }
        return new AccessReport(userToRepos);
    }

    private List<String> getRepos(String org) {
        return webClient.get()
                .uri("/orgs/{org}/repos", org)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Failed to get repos for org " + org + ": " + clientResponse.statusCode() + " " + body))))
                .bodyToFlux(Map.class)
                .map(repo -> (String) repo.get("name"))
                .collectList()
                .block();
    }

    private Mono<List<User>> getCollaboratorsMono(String org, String repo) {
        return webClient.get()
                .uri("/repos/{org}/{repo}/collaborators", org, repo)
                .header("Authorization", "Bearer " + token)
                .exchangeToFlux(response -> {
                    if (response.statusCode() == HttpStatus.FORBIDDEN) {
                        System.err.println("403 Forbidden on collaborators: " + org + "/" + repo + " (token lacks permission or repo is private)");
                        return reactor.core.publisher.Flux.empty();
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToFlux(Map.class);
                    }
                    return response.createException().flatMapMany(Mono::error);
                })
                .map(this::mapToUser)
                .collectList();
    }

    private User mapToUser(Map<String, Object> map) {
        String login = (String) map.get("login");
        // GitHub API for collaborators includes permissions
        Map<String, Object> permissions = (Map<String, Object>) map.get("permissions");
        String role = determineRole(permissions);
        return new User(login, role);
    }

    private String determineRole(Map<String, Object> permissions) {
        if (permissions != null) {
            Boolean admin = (Boolean) permissions.get("admin");
            Boolean push = (Boolean) permissions.get("push");
            Boolean pull = (Boolean) permissions.get("pull");
            if (admin != null && admin) return "admin";
            if (push != null && push) return "push";
            if (pull != null && pull) return "pull";
        }
        return "read"; // default
    }
}