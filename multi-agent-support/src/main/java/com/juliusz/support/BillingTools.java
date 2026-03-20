package com.juliusz.support;

import java.util.UUID;

/**
 * Simple in-memory implementation of billing-related tools.
 * In a real system, these would call databases or external services.
 */
public class BillingTools {

    /**
     * Confirm the customer's current plan and monthly price.
     */
    public String confirmPlan(String customerId) {
        // Stub: in a real system this would look up the plan in a database.
        String currentPlan = "Pro";
        double monthlyPrice = 49.0;

        return "Customer " + customerId + " is currently on the " + currentPlan +
                " plan at $" + monthlyPrice + " per month.";
    }

    /**
     * Open a refund case for the customer with a given reason.
     */
    public String openRefundCase(String customerId, String reason) {
        // Stub: create a fake case ID and return a confirmation message.
        String caseId = UUID.randomUUID().toString();

        return "I have opened a refund case for customer " + customerId +
                ".\nCase ID: " + caseId +
                "\nReason: \"" + reason + "\"\n" +
                "Our billing team will review it within 3–5 business days.";
    }

    /**
     * Explain the generic refund policy and timelines.
     */
    public String explainRefundPolicy() {
        // Stub: simple hard-coded policy description.
        return "According to our refund policy, you can request a refund within 30 days " +
                "of the charge date. Approved refunds are usually processed within 5–10 " +
                "business days back to the original payment method.";
    }

    /**
     * Return a simple, hard-coded billing history for the customer.
     */
    public String getBillingHistory(String customerId) {
        // Stub: in a real system this would query invoices/transactions.
        return "Billing history for customer " + customerId + ":\n" +
                "- 2025-02-01: $49.00 (Pro plan)\n" +
                "- 2025-03-01: $49.00 (Pro plan)\n" +
                "- 2025-04-01: $49.00 (Pro plan)";
    }
}
