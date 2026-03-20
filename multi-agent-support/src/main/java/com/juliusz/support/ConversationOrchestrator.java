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
     * Handle a new user message:
     * - update context,
     * - use LLM triage to create tasks,
     * - pick the next NEW task (non-TRIAGE),
     * - route to the appropriate agent,
     * - return agent's response.
     */
    public String handleUserMessage(String userMessage) {
        context.addUserMessage(userMessage);

        // 1. LLM triage → nowe taski
        List<ConversationTask> newTasks = triageAgent.analyze(userMessage);
        tasks.addAll(newTasks);

        // 2. Wybierz pierwszy NEW task, który ma konkretną kategorię
        ConversationTask nextTask = tasks.stream()
                .filter(t -> t.getStatus() == ConversationTask.TaskStatus.NEW)
                .filter(t -> t.getCategory() != ConversationTask.TaskCategory.TRIAGE)
                .findFirst()
                .orElse(null);

        if (nextTask == null) {
            // Nic nie jest jednoznacznie TECHNICAL/BILLING → globalny out-of-scope
            String outOfScope = """
                    I’m sorry, but I cannot assist with that request.
                    Please contact our general support team.
                    """;
            context.addAgentMessage(outOfScope);
            return outOfScope;
        }

        nextTask.setStatus(ConversationTask.TaskStatus.IN_PROGRESS);

        // 3. Zrutuj task do odpowiedniego agenta
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
}
