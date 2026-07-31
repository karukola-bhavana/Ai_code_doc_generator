package com.bhavana.aidoc.dto;

public class DocGenerationResponse {

    private RepositoryInfo repoInfo;
    private CodeAnalysisSummary analysisSummary;
    private String readmeMarkdown;
    private String apiDocMarkdown;
    private String architectureMarkdown;
    private String codeCommentsMarkdown;

    public DocGenerationResponse() {
    }

    public RepositoryInfo getRepoInfo() {
        return repoInfo;
    }

    public void setRepoInfo(RepositoryInfo repoInfo) {
        this.repoInfo = repoInfo;
    }

    public CodeAnalysisSummary getAnalysisSummary() {
        return analysisSummary;
    }

    public void setAnalysisSummary(CodeAnalysisSummary analysisSummary) {
        this.analysisSummary = analysisSummary;
    }

    public String getReadmeMarkdown() {
        return readmeMarkdown;
    }

    public void setReadmeMarkdown(String readmeMarkdown) {
        this.readmeMarkdown = readmeMarkdown;
    }

    public String getApiDocMarkdown() {
        return apiDocMarkdown;
    }

    public void setApiDocMarkdown(String apiDocMarkdown) {
        this.apiDocMarkdown = apiDocMarkdown;
    }

    public String getArchitectureMarkdown() {
        return architectureMarkdown;
    }

    public void setArchitectureMarkdown(String architectureMarkdown) {
        this.architectureMarkdown = architectureMarkdown;
    }

    public String getCodeCommentsMarkdown() {
        return codeCommentsMarkdown;
    }

    public void setCodeCommentsMarkdown(String codeCommentsMarkdown) {
        this.codeCommentsMarkdown = codeCommentsMarkdown;
    }
}
