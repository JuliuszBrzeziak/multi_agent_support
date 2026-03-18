package com.juliusz.support;

public class BillingAgent implements SupportAgent {

    @Override
    public String getName() {
        return "BillingAgent";
    }

    @Override
    public String respond(String userMessage, ConversationContext context) {
        // TODO: tu później:
        // 1) analiza intencji billingowej
        // 2) wywołania "tool-calli" typu openRefundCase(), confirmPlan(), itd.
        return "BillingAgent: (stub) odpowiedź billingowa na: " + userMessage;
    }
}
