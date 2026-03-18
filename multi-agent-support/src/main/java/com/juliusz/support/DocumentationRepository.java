package com.juliusz.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DocumentationRepository {

    private final Map<String, String> documents = new HashMap<>();

    public DocumentationRepository() {
        // Load all documentation files from resources/docs
        loadDocument("docs/integration_issues.txt");
        loadDocument("docs/configuration_tips.txt");
        loadDocument("docs/api_usage.txt");
    }

    private void loadDocument(String resourcePath) {
        String content = readResourceAsString(resourcePath);
        documents.put(resourcePath, content);
    }

    private String readResourceAsString(String resourcePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource: " + resourcePath, e);
        }
    }

    /**
     * Very simple relevance scoring: count how many query keywords
     * appear in each document and return the best matches.
     */
    public List<String> findRelevantSnippets(String userMessage, int maxDocs) {
        String[] keywords = userMessage.toLowerCase().split("\\s+");

        List<Map.Entry<String, String>> ranked = new ArrayList<>(documents.entrySet());
        ranked.sort((a, b) -> {
            int scoreA = score(a.getValue(), keywords);
            int scoreB = score(b.getValue(), keywords);
            return Integer.compare(scoreB, scoreA);
        });

        List<String> topSnippets = new ArrayList<>();
        for (Map.Entry<String, String> entry : ranked) {
            if (topSnippets.size() >= maxDocs) {
                break;
            }
            if (score(entry.getValue(), keywords) > 0) {
                topSnippets.add(entry.getValue());
            }
        }
        return topSnippets;
    }

    private int score(String content, String[] keywords) {
        String lower = content.toLowerCase();
        int score = 0;
        for (String kw : keywords) {
            if (kw.length() < 3) {
                continue; // ignore very short words
            }
            if (lower.contains(kw)) {
                score++;
            }
        }
        return score;
    }
}
