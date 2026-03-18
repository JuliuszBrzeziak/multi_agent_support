package com.juliusz.support;

public interface SupportAgent {
    String getName();

    String respond(String userMessage, ConversationContext context);
}
