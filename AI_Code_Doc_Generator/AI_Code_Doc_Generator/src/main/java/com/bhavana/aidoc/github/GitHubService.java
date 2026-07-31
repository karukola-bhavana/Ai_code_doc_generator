package com.bhavana.aidoc.github;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class GitHubService {

    @Value("${github.token:#{null}}")
    private String githubToken;

    public GHRepository getRepository(String owner, String repository) throws IOException {
        String token = (githubToken != null && !githubToken.isBlank()) ? githubToken : System.getenv("GITHUB_TOKEN");
        GitHub github;
        if (token != null && !token.isBlank()) {
            github = new GitHubBuilder().withOAuthToken(token).build();
        } else {
            github = GitHub.connectAnonymously();
        }
        return github.getRepository(owner + "/" + repository);
    }

    public Map<String, String> fetchJavaSourceFiles(GHRepository repo) {
        Map<String, String> files = new LinkedHashMap<>();
        try {
            fetchFilesRecursively(repo, "src/main/java", files, 15);
        } catch (Exception e) {
            // Ignore missing path or API rate limit
        }
        return files;
    }

    private void fetchFilesRecursively(GHRepository repo, String path, Map<String, String> result, int maxFiles) {
        if (result.size() >= maxFiles) return;
        try {
            List<org.kohsuke.github.GHContent> contents = repo.getDirectoryContent(path);
            for (org.kohsuke.github.GHContent content : contents) {
                if (result.size() >= maxFiles) break;
                if (content.isDirectory()) {
                    fetchFilesRecursively(repo, content.getPath(), result, maxFiles);
                } else if (content.isFile() && content.getName().endsWith(".java")) {
                    try (java.io.InputStream is = content.read()) {
                        String code = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                        result.put(content.getPath(), code);
                    }
                }
            }
        } catch (Exception e) {
            // Ignore missing path
        }
    }
}