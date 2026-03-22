package com.cloudeagle.githubaccessreport.model;

import java.util.List;
import java.util.Map;

public class AccessReport {
    private Map<String, List<RepositoryAccess>> userToRepos;

    public AccessReport() {}

    public AccessReport(Map<String, List<RepositoryAccess>> userToRepos) {
        this.userToRepos = userToRepos;
    }

    public Map<String, List<RepositoryAccess>> getUserToRepos() {
        return userToRepos;
    }

    public void setUserToRepos(Map<String, List<RepositoryAccess>> userToRepos) {
        this.userToRepos = userToRepos;
    }
}