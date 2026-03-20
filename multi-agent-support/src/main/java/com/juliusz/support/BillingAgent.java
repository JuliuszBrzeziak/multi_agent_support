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
    public String respond(String userMessage, ConversationContext context) {
        try {
            // 1. Poproś LLM o wybór toola i argumentów w formacie JSON
            String toolDecisionJson = askModelForToolDecision(userMessage);

            // 2. Sparsuj JSON (toolName + arguments)
            JsonNode root = objectMapper.readTree(toolDecisionJson);
            String toolName = root.path("toolName").asText(null);
            JsonNode args = root.path("arguments");

            if (toolName == null || toolName.isEmpty()) {
                return "BillingAgent: I couldn’t match your request to any billing action.";
            }

            // 3. Wywołaj odpowiedni BillingTools
            String toolResult;
            switch (toolName) {
                case "confirmPlan" -> {
                    String customerId = args.path("customerId").asText("demo-customer-id");
                    toolResult = tools.confirmPlan(customerId);
                }
                case "openRefundCase" -> {
                    String customerId = args.path("customerId").asText("demo-customer-id");
                    String reason = args.path("reason").asText(userMessage);
                    toolResult = tools.openRefundCase(customerId, reason);
                }
                case "explainRefundPolicy" -> {
                    toolResult = tools.explainRefundPolicy();
                }
                default -> {
                    toolResult = "BillingAgent: unknown billing tool: " + toolName;
                }
            }

            // 4. Poproś LLM o złożenie finalnej, ładnej odpowiedzi na bazie wyniku toola
            return summarizeToolResult(userMessage, toolName, toolResult);

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
                   - Use when the user mentions refunds, being charged twice, double charges, or similar.

                3) explainRefundPolicy()
                   - Use when the user asks about refund policy, refund timeline, or how refunds work.

                Your task:
                - Read the user's message.
                - Decide which single tool is the best to call (or none).
                - Construct appropriate arguments.

                Return ONLY a JSON object in this exact format:

                {
                  "toolName": "confirmPlan" | "openRefundCase" | "explainRefundPolicy" | null,
                  "arguments": {
                    // arguments for the selected tool
                  }
                }

                If you cannot decide which tool to call, set "toolName" to null.

                User message:
                """ + userMessage;

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