package com.juliusz.support;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        OpenAiChatClient chatClient = new OpenAiChatClient();
        ConversationOrchestrator orchestrator = new ConversationOrchestrator();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Multi-agent support console. Type 'exit' to quit.");

        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input.trim())) {
                break;
            }

            AgentReply reply = orchestrator.handleUserMessage(input);
            System.out.println(reply.agentName() + ": " + reply.message());
            
        }

        scanner.close();
    }
}
