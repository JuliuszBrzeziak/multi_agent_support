package com.juliusz.support;

public class ConversationTask {

    public enum TaskStatus {
        NEW,
        IN_PROGRESS,
        DONE
    }

    public enum TaskCategory {
        TRIAGE,
        TECHNICAL,
        BILLING
    }

    private final int id;
    private final String rawText;

    private TaskStatus status;
    private TaskCategory category;

    public ConversationTask(int id, String rawText) {
        this.id = id;
        this.rawText = rawText;
        this.status = TaskStatus.NEW;
        this.category = TaskCategory.TRIAGE;
    }

    public int getId() {
        return id;
    }

    public String getRawText() {
        return rawText;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public void setCategory(TaskCategory category) {
        this.category = category;
    }
}
