package com.bhavana.aidoc.github;

public class GitHubUrlParser {

    public GitHubResponse parse(String url) {

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("GitHub URL cannot be empty");
        }

        url = url.trim();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        String[] parts = url.split("/");

        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid GitHub URL");
        }

        String owner = parts[3];
        String repository = parts[4];

        return new GitHubResponse(owner, repository);
    }
}