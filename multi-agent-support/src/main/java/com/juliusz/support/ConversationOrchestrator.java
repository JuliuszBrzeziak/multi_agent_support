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

    /**
     * Handle a new user message.
     * Commands:
     * - "next"  -> process next pending task
     * - others  -> triage into tasks, then process next pending task
     */
    public String handleUserMessage(String userMessage) {
        String trimmed = userMessage.trim().toLowerCase();
        context.addUserMessage(userMessage);

        // Komenda sterująca: nie robimy triage, tylko bierzemy kolejny task
        if ("next".equals(trimmed)) {
            return handleNextTask();
        }

        // Normalny flow: nowa wiadomość → triage LLM → taski
        List<ConversationTask> newTasks = triageAgent.analyze(userMessage);
        tasks.addAll(newTasks);

        return handleNextTask();
    }

    private String handleNextTask() {
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

        nextTask.setStatus(ConversationTask.TaskStatus.IN_PROGRESS);

        String agentResponse;
        if (nextTask.getCategory() == ConversationTask.TaskCategory.TECHNICAL) {
            agentResponse = technicalAgent.respond(nextTask.getRawText(), context);
        } else {
            agentResponse = billingAgent.respond(nextTask.getRawText(), context);
        }

        nextTask.setStatus(ConversationTask.TaskStatus.DONE);

        context.addAgentMessage(agentResponse);
        return agentResponse;
    }

    /**
     * Tekstowy podgląd tasków (do komendy "status" w App).
     */
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
