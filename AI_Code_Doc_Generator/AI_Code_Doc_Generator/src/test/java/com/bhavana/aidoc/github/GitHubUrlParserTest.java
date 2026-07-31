package com.bhavana.aidoc.github;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GitHubUrlParserTest {

    private final GitHubUrlParser parser = new GitHubUrlParser();

    @Test
    void testValidGitHubUrl() {
        GitHubResponse response = parser.parse("https://github.com/octocat/Hello-World");
        assertEquals("octocat", response.getOwner());
        assertEquals("Hello-World", response.getRepository());
    }

    @Test
    void testValidGitHubUrlWithTrailingSlash() {
        GitHubResponse response = parser.parse("https://github.com/octocat/Hello-World/");
        assertEquals("octocat", response.getOwner());
        assertEquals("Hello-World", response.getRepository());
    }

    @Test
    void testInvalidUrlThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("invalid-url"));
    }

    @Test
    void testNullUrlThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
    }
}
