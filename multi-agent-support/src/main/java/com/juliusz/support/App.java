package com.juliusz.support;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        // 1. Inicjalizacja klienta LLM i agentów
        OpenAiChatClient chatClient = new OpenAiChatClient();
        TriageAgent triageAgent = new TriageAgent(chatClient);
        ConversationOrchestrator orchestrator = new ConversationOrchestrator(triageAgent);
        

        System.out.println("Multi-agent support chat. Type 'exit' to quit.");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String userInput = scanner.nextLine();
        
            if ("exit".equalsIgnoreCase(userInput.trim())) {
                break;
            }
        
            if ("status".equalsIgnoreCase(userInput.trim())) {
                System.out.println(orchestrator.getTasksStatus());
                continue;
            }
        
            String response = orchestrator.handleUserMessage(userInput);
            System.out.println(response);
        }
        

        scanner.close();
    }
}
