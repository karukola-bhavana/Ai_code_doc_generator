package com.bhavana.aidoc.dto;

public class RepositoryInfo {

    private String name;
    private String owner;
    private String description;
    private String language;
    private long stars;
    private long forks;

    public RepositoryInfo() {
    }

    public RepositoryInfo(String name, String owner, String description,
                          String language, long stars, long forks) {
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.language = language;
        this.stars = stars;
        this.forks = forks;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public long getStars() {
        return stars;
    }

    public long getForks() {
        return forks;
    }
}