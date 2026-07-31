package com.bhavana.aidoc.controller;

import com.bhavana.aidoc.dto.RepositoryInfo;
import com.bhavana.aidoc.github.GitHubResponse;
import com.bhavana.aidoc.github.GitHubService;
import com.bhavana.aidoc.github.GitHubUrlParser;
import org.kohsuke.github.GHRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitHubController {

    private final GitHubUrlParser parser = new GitHubUrlParser();
    private final GitHubService service;

    public GitHubController(GitHubService service) {
        this.service = service;
    }

    @GetMapping("/api/github/info")
    public RepositoryInfo getRepositoryInfo(@RequestParam String url) throws Exception {

        GitHubResponse response = parser.parse(url);

        GHRepository repository =
                service.getRepository(response.getOwner(), response.getRepository());

        return new RepositoryInfo(
                repository.getName(),
                repository.getOwnerName(),
                repository.getDescription(),
                repository.getLanguage(),
                repository.getStargazersCount(),
                repository.getForksCount()
        );
    }
}