package com.juliusz.support;

public class AgentReply {
    private final String agentName;
    private final String message;

    public AgentReply(String agentName, String message) {
        this.agentName = agentName;
        this.message = message;
    }

    public String agentName() { return agentName; }
    public String message() { return message; }
}
