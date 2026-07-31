package com.bhavana.aidoc.github;

public class GitHubResponse {

    private String owner;
    private String repository;

    public GitHubResponse() {
    }

    public GitHubResponse(String owner, String repository) {
        this.owner = owner;
        this.repository = repository;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }
}