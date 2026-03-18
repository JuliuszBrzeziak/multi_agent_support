package com.juliusz.support;

public class BillingAgent implements SupportAgent {

    private final BillingTools tools;

    public BillingAgent() {
        this.tools = new BillingTools();
    }

    @Override
    public String getName() {
        return "BillingAgent";
    }

    @Override
    public String respond(String userMessage, ConversationContext context) {
        // Very naive intent detection based on keywords.
        String lower = userMessage.toLowerCase();

        if (lower.contains("plan") || lower.contains("pricing") || lower.contains("subscription")) {
            // In a real system, customerId would come from context or auth
            return tools.confirmPlan("demo-customer-id");
        }

        if (lower.contains("refund") || lower.contains("charged twice") || lower.contains("double charge")) {
            return tools.openRefundCase("demo-customer-id", userMessage);
        }

        if (lower.contains("refund policy") || lower.contains("refund timeline")) {
            return tools.explainRefundPolicy();
        }

        // Default billing response
        return "BillingAgent: I can help with plans, pricing, invoices and refunds. " +
                "Could you please clarify your billing question?";
    }
}
