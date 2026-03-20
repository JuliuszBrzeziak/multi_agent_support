package com.juliusz.support;

import java.util.ArrayList;
import java.util.List;

public class ConversationOrchestrator {

    private final TriageAgent triageAgent;
    private final TechnicalAgent technicalAgent;
    private final BillingAgent billingAgent;

    private final ConversationContext context = new ConversationContext();
    private final List<ConversationTask> tasks = new ArrayList<>();

    public ConversationOrchestrator(TriageAgent triageAgent) {
        this.triageAgent = triageAgent;
        this.technicalAgent = new TechnicalAgent();
        this.billingAgent = new BillingAgent();
    }

    public String handleUserMessage(String userMessage) {
        String trimmed = userMessage.trim().toLowerCase();
        context.addUserMessage(userMessage);

        // Komendy sterujące
        if ("next".equals(trimmed)) {
            return handleNextTask(userMessage);
        }

        // 1. Jeśli jest billingowy task w IN_PROGRESS, kontynuuj go
        ConversationTask inProgressBilling = tasks.stream()
                .filter(t -> t.getStatus() == ConversationTask.TaskStatus.IN_PROGRESS)
                .filter(t -> t.getCategory() == ConversationTask.TaskCategory.BILLING)
                .findFirst()
                .orElse(null);

        if (inProgressBilling != null) {
            String response = billingAgent.respond(inProgressBilling, userMessage, context);
            context.addAgentMessage(response);
            return response;
        }

        // 2. Normalny flow: nowa wiadomość → triage LLM → taski
        List<ConversationTask> newTasks = triageAgent.analyze(userMessage);
        tasks.addAll(newTasks);

        return handleNextTask(userMessage);
    }

    private String handleNextTask(String userMessage) {
        // znajdź pierwszy NEW task z kategorią TECHNICAL/BILLING
        ConversationTask nextTask = tasks.stream()
                .filter(t -> t.getStatus() == ConversationTask.TaskStatus.NEW)
                .filter(t -> t.getCategory() != ConversationTask.TaskCategory.TRIAGE)
                .findFirst()
                .orElse(null);

        if (nextTask == null) {
            String outOfScope = """
                    I’m sorry, but I cannot assist with that request.
                    Please contact our general support team.
                    """;
            context.addAgentMessage(outOfScope);
            return outOfScope;
        }

        String agentResponse;
        if (nextTask.getCategory() == ConversationTask.TaskCategory.TECHNICAL) {
            nextTask.setStatus(ConversationTask.TaskStatus.IN_PROGRESS);
            agentResponse = technicalAgent.respond(nextTask, userMessage, context);
            nextTask.setStatus(ConversationTask.TaskStatus.DONE);
        } else {
            nextTask.setStatus(ConversationTask.TaskStatus.NEW); // BillingAgent sam ustawi IN_PROGRESS/DONE
            agentResponse = billingAgent.respond(nextTask, userMessage, context);
            // dla getBillingHistory BillingAgent może zostawić task w IN_PROGRESS
            if (nextTask.getStatus() == ConversationTask.TaskStatus.NEW) {
                nextTask.setStatus(ConversationTask.TaskStatus.DONE);
            }
        }

        context.addAgentMessage(agentResponse);
        return agentResponse;
    }

    public String getTasksStatus() {
        if (tasks.isEmpty()) {
            return "No tasks have been created yet.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Current tasks:\n");
        for (ConversationTask t : tasks) {
            sb.append("- [")
              .append(t.getStatus())
              .append("] ")
              .append(t.getCategory());
            if (t.getCategory() == ConversationTask.TaskCategory.TRIAGE) {
                sb.append(" (triage pending / out-of-scope candidate)");
            }
            sb.append(" (id=")
              .append(t.getId())
              .append("): ")
              .append(t.getRawText())
              .append("\n");
        }
        return sb.toString();
    }
}
