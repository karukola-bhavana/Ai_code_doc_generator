package com.bhavana.aidoc.dto;

import java.util.List;

public class DocGenerationRequest {

    private String repoUrl;
    private List<String> docTypes; // "readme", "api", "architecture", "comments"
    private String customInstructions;

    public DocGenerationRequest() {
    }

    public DocGenerationRequest(String repoUrl, List<String> docTypes, String customInstructions) {
        this.repoUrl = repoUrl;
        this.docTypes = docTypes;
        this.customInstructions = customInstructions;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public List<String> getDocTypes() {
        return docTypes;
    }

    public void setDocTypes(List<String> docTypes) {
        this.docTypes = docTypes;
    }

    public String getCustomInstructions() {
        return customInstructions;
    }

    public void setCustomInstructions(String customInstructions) {
        this.customInstructions = customInstructions;
    }
}
