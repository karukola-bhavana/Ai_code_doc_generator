package com.bhavana.aidoc.prompt;

import com.bhavana.aidoc.dto.RepositoryInfo;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildReadmePrompt(RepositoryInfo repoInfo, String codeAnalysis) {
        return """
                You are an expert technical writer and software architect.
                Generate a comprehensive, professional, and visually appealing README.md file in GitHub-Flavored Markdown for the following repository.
                
                Repository Name: %s
                Owner: %s
                Description: %s
                Primary Language: %s
                Stars: %d | Forks: %d
                
                AST Code Structure Analysis:
                %s
                
                Guidelines for README.md:
                1. Include GitHub Shields badges at the top (build, license, language, stars).
                2. Project Title with a compelling tagline.
                3. Key Features bullet list.
                4. Architecture / Directory Structure overview.
                5. Prerequisites & Quick Start / Installation instructions (e.g. Maven/Gradle build commands).
                6. API Endpoints overview table.
                7. Configuration & Environment variables guide.
                8. License and Contributing section.
                
                Output ONLY valid Markdown.
                """.formatted(
                repoInfo.getName(),
                repoInfo.getOwner(),
                repoInfo.getDescription() != null ? repoInfo.getDescription() : "N/A",
                repoInfo.getLanguage() != null ? repoInfo.getLanguage() : "Java",
                repoInfo.getStars(),
                repoInfo.getForks(),
                codeAnalysis
        );
    }

    public String buildApiDocPrompt(RepositoryInfo repoInfo, String codeAnalysis) {
        return """
                You are a senior API Architect.
                Generate detailed REST API Documentation in GitHub-Flavored Markdown based on the code analysis below.
                
                Repository: %s/%s
                Code & Endpoints Analysis:
                %s
                
                Guidelines for API Documentation:
                1. Table of Contents listing all endpoints.
                2. For each REST endpoint:
                   - Endpoint Path & HTTP Method (e.g. GET, POST)
                   - Brief Description & Purpose
                   - Request Parameters (Query params, Path variables) & Headers
                   - Request Body format (JSON sample)
                   - Response Payload JSON schema with example output
                   - HTTP Status codes (200 OK, 400 Bad Request, 500 Internal Error)
                
                Output ONLY valid Markdown.
                """.formatted(repoInfo.getOwner(), repoInfo.getName(), codeAnalysis);
    }

    public String buildArchitecturePrompt(RepositoryInfo repoInfo, String codeAnalysis) {
        return """
                You are a Principal Software System Architect.
                Generate a comprehensive Architecture Overview Document in GitHub-Flavored Markdown for the project %s/%s.
                
                Code Structure & AST Analysis:
                %s
                
                Guidelines for Architecture Document:
                1. High-Level Architectural Summary & Design Patterns (e.g., MVC, Layered Architecture, Spring Boot REST service).
                2. Component Breakdown (Controllers, Services, Repositories, Parsers, External Integrations).
                3. Include a Mermaid JS diagram (` ```mermaid ... ``` `) visualizing the component interaction flow (User -> Controller -> Service -> External API / Parser).
                4. Data Flow & Tech Stack Specifications.
                5. Scalability & Security Considerations.
                
                Output ONLY valid Markdown.
                """.formatted(repoInfo.getOwner(), repoInfo.getName(), codeAnalysis);
    }

    public String buildCodeCommentsPrompt(RepositoryInfo repoInfo, String codeAnalysis) {
        return """
                You are a Lead Software Developer enforcing clean code and documentation standard.
                Generate Javadoc & Inline Code Comments suggestions in GitHub-Flavored Markdown for the analyzed codebase.
                
                Repository: %s/%s
                Class & Method Signatures:
                %s
                
                Guidelines:
                1. Provide standard Javadoc comments (@param, @return, @throws) for all classes and public methods discovered.
                2. Highlight best practices, thread-safety tips, and potential refactoring enhancements.
                3. Include code snippets demonstrating properly annotated Java classes.
                
                Output ONLY valid Markdown.
                """.formatted(repoInfo.getOwner(), repoInfo.getName(), codeAnalysis);
    }
}
