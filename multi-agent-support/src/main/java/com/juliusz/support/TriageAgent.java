package com.juliusz.support;

import java.util.ArrayList;
import java.util.List;

public class TriageAgent {

    private final OpenAiChatClient chatClient;
    private int nextTaskId = 1;

    public TriageAgent(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public List<ConversationTask> analyze(String userMessage) {
        String prompt = buildPrompt(userMessage);

        String llmResponseJson = chatClient.sendSingleTurnPrompt(prompt);

        return parseTasks(llmResponseJson);
    }

    private String buildPrompt(String userMessage) {
        return """
            You are a triage agent for a customer support system.

            Your task:
            - Read the following user message.
            - Extract all distinct problems mentioned in the message.
            - For each problem, assign a category from this set:
              - TECHNICAL
              - BILLING
              - TRIAGE (if you are not sure or the problem does not clearly fit the other categories).

            Return ONLY a valid JSON array.
            Each element must have:
              - "text": short description of the problem
              - "category": one of "TECHNICAL", "BILLING", "TRIAGE".

            User message:
            """ + userMessage;
    }

    private List<ConversationTask> parseTasks(String llmResponseJson) {
        List<ConversationTask> tasks = new ArrayList<>();

        // Pseudokod z Jacksonem:
        //
        // ObjectMapper mapper = new ObjectMapper();
        // ArrayNode array = (ArrayNode) mapper.readTree(llmResponseJson);
        // for (JsonNode node : array) {
        //     String text = node.get("text").asText();
        //     String categoryStr = node.get("category").asText();
        //
        //     ConversationTask task = new ConversationTask(nextTaskId++, text);
        //     task.setCategory(parseCategory(categoryStr));
        //     tasks.add(task);
        // }
        //
        return tasks;
    }

    private ConversationTask.TaskCategory parseCategory(String categoryStr) {
        try {
            return ConversationTask.TaskCategory.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ConversationTask.TaskCategory.TRIAGE;
        }
    }
}
