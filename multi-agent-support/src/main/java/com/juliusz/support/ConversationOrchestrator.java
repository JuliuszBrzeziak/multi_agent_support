package com.juliusz.support;

import java.util.ArrayList;
import java.util.List;

public class ConversationOrchestrator {

    private final OpenAiChatClient chatClient;
    private final List<String> history = new ArrayList<>();

    public ConversationOrchestrator(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String handleUserMessage(String userMessage) {
        history.add("USER: " + userMessage);

        String reply = chatClient.sendMessage(history);

        history.add("ASSISTANT: " + reply);
        return reply;
    }
}
