package com.juliusz.support;

public class TechnicalAgent implements SupportAgent {

    @Override
    public String getName() {
        return "TechnicalAgent";
    }

    @Override
    public String respond(String userMessage, ConversationContext context) {
        // TODO: tu później:
        // 1) znajdowanie odpowiednich fragmentów dokumentacji
        // 2) wołanie LLM z tymi fragmentami
        return "TechnicalAgent: (stub) odpowiedź techniczna na: " + userMessage;
    }
}
