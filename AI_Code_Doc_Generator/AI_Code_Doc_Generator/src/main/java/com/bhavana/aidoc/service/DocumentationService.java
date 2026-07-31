package com.bhavana.aidoc.service;

import com.bhavana.aidoc.dto.*;
import com.bhavana.aidoc.github.GitHubResponse;
import com.bhavana.aidoc.github.GitHubService;
import com.bhavana.aidoc.github.GitHubUrlParser;
import com.bhavana.aidoc.parser.JavaCodeParser;
import com.bhavana.aidoc.prompt.PromptBuilder;
import org.kohsuke.github.GHRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DocumentationService {

    private final GitHubUrlParser urlParser = new GitHubUrlParser();
    private final GitHubService gitHubService;
    private final JavaCodeParser codeParser;
    private final PromptBuilder promptBuilder;
    private final GeminiService geminiService;

    public DocumentationService(GitHubService gitHubService,
                                JavaCodeParser codeParser,
                                PromptBuilder promptBuilder,
                                GeminiService geminiService) {
        this.gitHubService = gitHubService;
        this.codeParser = codeParser;
        this.promptBuilder = promptBuilder;
        this.geminiService = geminiService;
    }

    public DocGenerationResponse generateDocumentation(DocGenerationRequest request) throws Exception {
        DocGenerationResponse response = new DocGenerationResponse();

        // Step 1: Parse GitHub Repo URL & fetch Repository Info
        GitHubResponse parsedUrl = urlParser.parse(request.getRepoUrl());
        GHRepository ghRepo = gitHubService.getRepository(parsedUrl.getOwner(), parsedUrl.getRepository());

        RepositoryInfo repoInfo = new RepositoryInfo(
                ghRepo.getName(),
                ghRepo.getOwnerName(),
                ghRepo.getDescription(),
                ghRepo.getLanguage(),
                ghRepo.getStargazersCount(),
                ghRepo.getForksCount()
        );
        response.setRepoInfo(repoInfo);

        // Step 2: Fetch Java Source Files & Run AST Parsing
        Map<String, String> javaFiles = gitHubService.fetchJavaSourceFiles(ghRepo);
        List<JavaCodeParser.ClassInfo> parsedClasses = new ArrayList<>();

        int endpointsCount = 0;
        for (Map.Entry<String, String> entry : javaFiles.entrySet()) {
            JavaCodeParser.ClassInfo classInfo = codeParser.parseJavaCode(entry.getValue());
            if (classInfo.getName() != null && !classInfo.getName().equals("Unknown / Unparsed")) {
                parsedClasses.add(classInfo);
                endpointsCount += classInfo.getRestEndpoints().size();
            }
        }

        String structuredCodeAnalysis = codeParser.summarizeClasses(parsedClasses);

        CodeAnalysisSummary analysisSummary = new CodeAnalysisSummary();
        analysisSummary.setTotalFilesAnalyzed(javaFiles.size());
        analysisSummary.setTotalClassesFound(parsedClasses.size());
        analysisSummary.setTotalEndpointsFound(endpointsCount);
        analysisSummary.setDetectedTechnologies(List.of("Java", "Spring Boot", "Maven/Gradle"));
        analysisSummary.setStructuredAnalysisText(structuredCodeAnalysis);
        response.setAnalysisSummary(analysisSummary);

        List<String> docTypes = request.getDocTypes();
        if (docTypes == null || docTypes.isEmpty()) {
            docTypes = List.of("readme", "api", "architecture", "comments");
        }

        // Step 3: Call Gemini AI for selected documentation types
        if (docTypes.contains("readme")) {
            String readmePrompt = promptBuilder.buildReadmePrompt(repoInfo, structuredCodeAnalysis);
            response.setReadmeMarkdown(geminiService.generateText(readmePrompt));
            pauseForRateLimit();
        }

        if (docTypes.contains("api")) {
            String apiPrompt = promptBuilder.buildApiDocPrompt(repoInfo, structuredCodeAnalysis);
            response.setApiDocMarkdown(geminiService.generateText(apiPrompt));
            pauseForRateLimit();
        }

        if (docTypes.contains("architecture")) {
            String archPrompt = promptBuilder.buildArchitecturePrompt(repoInfo, structuredCodeAnalysis);
            response.setArchitectureMarkdown(geminiService.generateText(archPrompt));
            pauseForRateLimit();
        }

        if (docTypes.contains("comments")) {
            String commentsPrompt = promptBuilder.buildCodeCommentsPrompt(repoInfo, structuredCodeAnalysis);
            response.setCodeCommentsMarkdown(geminiService.generateText(commentsPrompt));
        }

        return response;
    }

    private void pauseForRateLimit() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ignored) {}
    }
}
