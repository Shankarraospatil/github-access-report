package com.cloudeagle.githubaccessreport.model;

import java.util.List;

public class Repository {
    private String name;
    private List<User> collaborators;

    public Repository() {}

    public Repository(String name, List<User> collaborators) {
        this.name = name;
        this.collaborators = collaborators;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<User> collaborators) {
        this.collaborators = collaborators;
    }
}