package com.bhavana.aidoc.dto;

import java.util.ArrayList;
import java.util.List;

public class CodeAnalysisSummary {

    private int totalFilesAnalyzed;
    private int totalClassesFound;
    private int totalEndpointsFound;
    private List<String> detectedTechnologies = new ArrayList<>();
    private String structuredAnalysisText;

    public CodeAnalysisSummary() {
    }

    public int getTotalFilesAnalyzed() {
        return totalFilesAnalyzed;
    }

    public void setTotalFilesAnalyzed(int totalFilesAnalyzed) {
        this.totalFilesAnalyzed = totalFilesAnalyzed;
    }

    public int getTotalClassesFound() {
        return totalClassesFound;
    }

    public void setTotalClassesFound(int totalClassesFound) {
        this.totalClassesFound = totalClassesFound;
    }

    public int getTotalEndpointsFound() {
        return totalEndpointsFound;
    }

    public void setTotalEndpointsFound(int totalEndpointsFound) {
        this.totalEndpointsFound = totalEndpointsFound;
    }

    public List<String> getDetectedTechnologies() {
        return detectedTechnologies;
    }

    public void setDetectedTechnologies(List<String> detectedTechnologies) {
        this.detectedTechnologies = detectedTechnologies;
    }

    public String getStructuredAnalysisText() {
        return structuredAnalysisText;
    }

    public void setStructuredAnalysisText(String structuredAnalysisText) {
        this.structuredAnalysisText = structuredAnalysisText;
    }
}
