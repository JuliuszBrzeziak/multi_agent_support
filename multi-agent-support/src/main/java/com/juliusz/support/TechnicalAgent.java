package com.juliusz.support;

import java.util.List;
import java.util.stream.Collectors;

public class TechnicalAgent implements SupportAgent {

    private final DocumentationRepository docs;
    private final OpenAiChatClient chatClient;

    public TechnicalAgent() {
        this.docs = new DocumentationRepository();
        this.chatClient = new OpenAiChatClient();
    }

    @Override
    public String getName() {
        return "TechnicalAgent";
    }

    @Override
    public String respond(ConversationTask task, String userMessage, ConversationContext context) {
        // używamy treści taska jako pytania technicznego
        String question = task.getRawText();

        // 1) Retrieve relevant documentation snippets
        List<String> snippets = docs.findRelevantSnippets(question, 2);

        if (snippets.isEmpty()) {
            return "TechnicalAgent: I could not find relevant information in the documentation. " +
                    "Could you clarify your question or check with general support?";
        }

        // 2) Build a context string from the snippets
        String contextText = snippets.stream()
                .map(s -> "---\n" + s)
                .collect(Collectors.joining("\n"));

        // 3) Ask the LLM to answer strictly based on this context
        String prompt = """
                You are a technical support specialist.
                Answer the user's question ONLY using the context below.
                If the answer is not in the context, say that the documentation does not cover it.

                CONTEXT:
                %s

                USER QUESTION:
                %s
                """.formatted(contextText, question);

        String answer = chatClient.sendSingleTurnPrompt(prompt);
        return "TechnicalAgent: " + answer;
    }
}
