package com.juliusz.support;

public class ConversationOrchestrator {

    private final TechnicalAgent technicalAgent;
    private final BillingAgent billingAgent;
    private final ConversationContext context = new ConversationContext();

    public ConversationOrchestrator() {
        this.technicalAgent = new TechnicalAgent();
        this.billingAgent = new BillingAgent();
    }

    public String handleUserMessage(String userMessage) {
        context.addUserMessage(userMessage);

        String lower = userMessage.toLowerCase();

        boolean isBilling =
                lower.contains("plan")
                        || lower.contains("pricing")
                        || lower.contains("subscription")
                        || lower.contains("refund")
                        || lower.contains("charged twice")
                        || lower.contains("double charge")
                        || lower.contains("invoice")
                        || lower.contains("payment");

        String agentResponse;
        if (isBilling) {
            agentResponse = billingAgent.respond(userMessage, context);
        } else {
            agentResponse = technicalAgent.respond(userMessage, context);
        }

        context.addAgentMessage(agentResponse);
        return agentResponse;
    }
}
