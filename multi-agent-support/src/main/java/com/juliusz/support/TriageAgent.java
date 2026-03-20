package com.juliusz.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class TriageAgent {

    private final OpenAiChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private int nextTaskId = 1;

    public TriageAgent(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Analyze the user's message and return a list of conversation tasks
     * with initial categories (TECHNICAL/BILLING/TRIAGE).
     */
    public List<ConversationTask> analyze(String userMessage) {
        String prompt = buildPrompt(userMessage);
        String llmResponseJson = chatClient.sendSingleTurnPrompt(prompt);

        return parseTasks(llmResponseJson);
    }

    private String buildPrompt(String userMessage) {
        return """
                You are a triage agent for a customer support system.

                You see a single user message that may contain one or multiple distinct problems.
                Your job is to extract each problem and assign it to a category:

                - TECHNICAL: integration issues, API problems, setup/configuration errors, 500 errors, etc.
                - BILLING: plans, pricing, subscriptions, invoices, payments, refunds, double charges, billing history.
                - TRIAGE: if you are not sure which category fits best.

                Return ONLY a JSON array with no extra text.
                Each element must be an object with:
                  - "text": short description or the exact fragment of the problem
                  - "category": one of "TECHNICAL", "BILLING", "TRIAGE"

                Example:
                User: "My integration with HubSpot keeps failing and I think I was charged twice this month."
                [
                  {
                    "text": "Integration with HubSpot keeps failing",
                    "category": "TECHNICAL"
                  },
                  {
                    "text": "Charged twice this month",
                    "category": "BILLING"
                  }
                ]

                Now process this user message and return ONLY the JSON array:

                User: %s
                """.formatted(userMessage);
    }

    private List<ConversationTask> parseTasks(String llmResponseJson) {
        List<ConversationTask> tasks = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(llmResponseJson);

            if (!root.isArray()) {
                // jeśli model zawali format, potraktuj całą wiadomość jako jeden TRIAGE task
                ConversationTask fallback = new ConversationTask(nextTaskId++, llmResponseJson);
                fallback.setCategory(ConversationTask.TaskCategory.TRIAGE);
                tasks.add(fallback);
                return tasks;
            }

            for (JsonNode node : root) {
                String text = node.path("text").asText("").trim();
                String categoryStr = node.path("category").asText("TRIAGE").trim();

                if (text.isEmpty()) {
                    continue;
                }

                ConversationTask task = new ConversationTask(nextTaskId++, text);
                task.setCategory(parseCategory(categoryStr));
                tasks.add(task);
            }

            if (tasks.isEmpty()) {
                ConversationTask fallback = new ConversationTask(nextTaskId++, "Uncategorized: " + llmResponseJson);
                fallback.setCategory(ConversationTask.TaskCategory.TRIAGE);
                tasks.add(fallback);
            }

        } catch (Exception e) {
            ConversationTask fallback = new ConversationTask(nextTaskId++, "Parse error for: " + llmResponseJson);
            fallback.setCategory(ConversationTask.TaskCategory.TRIAGE);
            tasks.add(fallback);
        }

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
