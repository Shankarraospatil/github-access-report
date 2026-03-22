package com.cloudeagle.githubaccessreport.model;

public class RepositoryAccess {
    private String repoName;
    private String permission;

    public RepositoryAccess() {}

    public RepositoryAccess(String repoName, String permission) {
        this.repoName = repoName;
        this.permission = permission;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}