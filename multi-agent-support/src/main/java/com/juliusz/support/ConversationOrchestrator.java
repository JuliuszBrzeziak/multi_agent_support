package com.juliusz.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConversationOrchestrator {

    private final OpenAiChatClient chatClient;
    private final TechnicalAgent technicalAgent;
    private final BillingAgent billingAgent;
    private final ConversationContext context;

    // Simple text history; later we can replace this with a richer message model
    private final List<String> history = new ArrayList<>();

    public ConversationOrchestrator(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
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
    public String handleUserMessage(String userMessage) {
        history.add("USER: " + userMessage);
        context.addMessage("USER: " + userMessage);   // <--- now using context
    
        SupportAgent selectedAgent = routeToAgent(userMessage);
    
        String agentReply = selectedAgent.respond(userMessage, context);
    
        history.add(selectedAgent.getName() + ": " + agentReply);
        context.addMessage(selectedAgent.getName() + ": " + agentReply);   // <---
    
        return agentReply;
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
