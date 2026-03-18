package com.juliusz.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared state for the current conversation.
 * Agents can read/write information here instead of passing many parameters.
 */
public class ConversationContext {

    // Simple in-memory history of exchanged messages (for future use).
    private final List<String> messageHistory = new ArrayList<>();

    public void addMessage(String message) {
        messageHistory.add(message);
    }

    public List<String> getMessageHistory() {
        return messageHistory;
    }
}
