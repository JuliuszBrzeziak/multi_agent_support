package com.juliusz.support;

import java.util.Locale;

public class ConversationOrchestrator {

    private final TechnicalAgent technicalAgent;
    private final BillingAgent billingAgent;
    private final ConversationContext context;

    public ConversationOrchestrator() {
        this.technicalAgent = new TechnicalAgent();
        this.billingAgent = new BillingAgent();
        this.context = new ConversationContext();
    }

    /**
     * Handle a single user message:
     *  - update conversation history
     *  - route to the most appropriate agent
     *  - let the selected agent generate a reply
     */
    public AgentReply handleUserMessage(String userMessage) {
        context.addMessage("USER: " + userMessage);
    
        SupportAgent selectedAgent = routeToAgent(userMessage);
    
        String agentReply = selectedAgent.respond(userMessage, context);
    
        context.addMessage(selectedAgent.getName() + ": " + agentReply);
    
        return new AgentReply(selectedAgent.getName(), agentReply);
    }
    

    /**
     * Very simple routing based on keywords in the user message.
     * Later we can replace this with an LLM-based classifier.
     */
    private SupportAgent routeToAgent(String userMessage) {
        String lower = userMessage.toLowerCase(Locale.ROOT);

        if (lower.contains("invoice")
                || lower.contains("refund")
                || lower.contains("billing")
                || lower.contains("charged")
                || lower.contains("payment")) {
            return billingAgent;
        }

        // Default route: technical questions
        return technicalAgent;
    }
}
