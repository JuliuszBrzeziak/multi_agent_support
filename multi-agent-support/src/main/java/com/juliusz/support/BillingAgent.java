package com.juliusz.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillingAgent implements SupportAgent {

    private final BillingTools tools;
    private final OpenAiChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BillingAgent() {
        this.tools = new BillingTools();
        this.chatClient = new OpenAiChatClient();
    }

    @Override
    public String getName() {
        return "BillingAgent";
    }

    @Override
    public String respond(ConversationTask task, String userMessage, ConversationContext context) {
        try {
            // 0. Drugi krok: task jest w trakcie (IN_PROGRESS) → userMessage to customerId
            if (task.getStatus() == ConversationTask.TaskStatus.IN_PROGRESS
                    && task.getCategory() == ConversationTask.TaskCategory.BILLING) {

                String trimmed = userMessage.trim();

                // Prosta walidacja: ID musi mieć jakieś cyfry – jeśli nie, dopytaj
                boolean looksLikeId = trimmed.matches(".*\\d.*"); // zawiera przynajmniej jedną cyfrę

                if (!looksLikeId) {
                    // NIE zmieniamy statusu, dalej czekamy na poprawne ID
                    return """
                            BillingAgent: I still need your customer ID to show your billing history.
                            Please provide the ID (for example: 123-456-789).
                            """;
                }

                String customerId = trimmed;
                String toolResult = tools.getBillingHistory(customerId);

                task.setStatus(ConversationTask.TaskStatus.DONE);

                return summarizeToolResult(
                        userMessage,
                        "getBillingHistory",
                        toolResult
                );
            }

            // 1. Nowy task – normalny LLM tool-calling na treści taska
            String toolDecisionJson = askModelForToolDecision(task.getRawText());

            JsonNode root = objectMapper.readTree(toolDecisionJson);
            String toolName = root.path("toolName").asText(null);
            JsonNode args = root.path("arguments");

            // brak wybranego toola → dopytaj i trzymaj task jako IN_PROGRESS
            if (toolName == null || toolName.isEmpty() || "null".equalsIgnoreCase(toolName)) {
                task.setStatus(ConversationTask.TaskStatus.IN_PROGRESS);
                return """
                        BillingAgent: I see you mentioned a billing topic, but I'm not sure what you want me to do.
                        I can:
                        - confirm your current plan and pricing,
                        - open a refund case,
                        - explain our refund policy,
                        - show your recent billing history.

                        Could you please clarify what exactly you need help with?
                        """;
            }

            String toolResult;
            switch (toolName) {
                case "confirmPlan" -> {
                    String customerId = args.path("customerId").asText("demo-customer-id");
                    toolResult = tools.confirmPlan(customerId);
                    task.setStatus(ConversationTask.TaskStatus.DONE);
                }
                case "openRefundCase" -> {
                    String customerId = args.path("customerId").asText("demo-customer-id");
                    String reason = args.path("reason").asText(task.getRawText());
                    toolResult = tools.openRefundCase(customerId, reason);
                    task.setStatus(ConversationTask.TaskStatus.DONE);
                }
                case "explainRefundPolicy" -> {
                    toolResult = tools.explainRefundPolicy();
                    task.setStatus(ConversationTask.TaskStatus.DONE);
                }
                case "getBillingHistory" -> {
                    // Krok 1 z 2: prosimy o ID i zostawiamy task w IN_PROGRESS
                    task.setStatus(ConversationTask.TaskStatus.IN_PROGRESS);
                    return "BillingAgent: To show your billing history, please provide your customer ID.";
                }
                default -> {
                    toolResult = "BillingAgent: unknown billing tool: " + toolName;
                    task.setStatus(ConversationTask.TaskStatus.DONE);
                }
            }

            return summarizeToolResult(task.getRawText(), toolName, toolResult);

        } catch (Exception e) {
            return "BillingAgent: something went wrong while processing your billing request.";
        }
    }

    private String askModelForToolDecision(String userMessage) {
        String prompt = """
                You are a billing assistant. You have access to these tools:

                1) confirmPlan(customerId: string)
                   - Use when the user asks about their current plan, pricing, or subscription.

                2) openRefundCase(customerId: string, reason: string)
                   - Use when the user mentions refunds, being charged twice, double charges, or payment problems.

                3) explainRefundPolicy()
                   - Use when the user asks about refund policy, refund timeline, or how refunds work.

                4) getBillingHistory(customerId: string)
                   - Use when the user asks about billing history, past invoices, or previous charges.

                Your task:
                - Read the user's message.
                - Decide which SINGLE tool is the best to call.
                - Construct appropriate arguments.
                - If the user's request is too vague to choose a tool, set "toolName" to null and "arguments" to {}.

                Return ONLY a JSON object in this exact format, with no extra text:

                {
                  "toolName": "confirmPlan" | "openRefundCase" | "explainRefundPolicy" | "getBillingHistory" | null,
                  "arguments": {
                    // arguments for the selected tool
                  }
                }

                Example 1:
                User: "I was charged twice this month"
                {
                  "toolName": "openRefundCase",
                  "arguments": {
                    "customerId": "demo-customer-id",
                    "reason": "User was charged twice this month"
                  }
                }

                Example 2:
                User: "What plan am I on and how much does it cost?"
                {
                  "toolName": "confirmPlan",
                  "arguments": {
                    "customerId": "demo-customer-id"
                  }
                }

                Example 3:
                User: "Can you show me my recent invoices?"
                {
                  "toolName": "getBillingHistory",
                  "arguments": {
                    "customerId": "demo-customer-id"
                  }
                }

                Now process the following user message and return ONLY the JSON:

                User: %s
                """.formatted(userMessage);

        return chatClient.sendSingleTurnPrompt(prompt);
    }

    private String summarizeToolResult(String userMessage, String toolName, String toolResult) {
        String prompt = """
                You are a billing specialist.

                The user asked:
                %s

                You (the system) invoked the tool:
                %s

                The tool returned this result:
                %s

                Based on this tool result, write a clear, concise answer to the user.
                Do not mention tools or internal functions in your answer.
                """.formatted(userMessage, toolName, toolResult);

        return chatClient.sendSingleTurnPrompt(prompt);
    }
}
