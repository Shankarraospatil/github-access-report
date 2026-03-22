package com.cloudeagle.githubaccessreport.controller;

import com.cloudeagle.githubaccessreport.model.AccessReport;
import com.cloudeagle.githubaccessreport.service.GithubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access-report")
public class AccessReportController {

    private final GithubService githubService;

    public AccessReportController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/{org}")
    public AccessReport getAccessReport(@PathVariable String org) {
        return githubService.generateReport(org);
    }
}