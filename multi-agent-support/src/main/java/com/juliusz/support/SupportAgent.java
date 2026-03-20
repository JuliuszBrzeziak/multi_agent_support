package com.juliusz.support;

public interface SupportAgent {
    String getName();
    String respond(ConversationTask task, String userMessage, ConversationContext context);
}
