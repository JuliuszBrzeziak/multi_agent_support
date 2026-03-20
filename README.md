# multi_agent_support

Overview

This project implements a conversational support system in Java with two collaborating AI agents within a single chat: a Technical Specialist and a Billing Specialist.​
The system routes each user message to the most appropriate agent, supports multi‑turn conversations, and dynamically switches agents within the same conversation.


Architecture
Conversation orchestration

    A central conversation orchestrator maintains a list of conversation tasks.

    Each task has:

        category: TECHNICAL, BILLING, or TRIAGE (out‑of‑scope),

        status: NEW, IN_PROGRESS, DONE,

        id and a short description.

    A simple triage step classifies new user messages into categories and creates tasks accordingly.​

    Only one task is active at a time; the orchestrator decides which agent should respond based on the task category and status.​

TechnicalAgent (Technical Specialist)

    Answers technical questions using only a small set of local documentation files.​

    On each technical question:

        retrieves the most relevant documentation snippets using local search (e.g. semantic / vector or keyword),

        passes only those snippets as context to the LLM,

        generates an answer strictly grounded in the provided documents.​

    If the information is not present in the documentation, it either:

        asks the user for clarification, or

        clearly states that the information is not available, without guessing.​

BillingAgent (Billing Specialist)

    Handles billing‑related questions and requests using LLM tool calling.​

    Exposes several backend capabilities, for example:

        confirm customer plan and pricing,

        open a refund case,

        explain refund policy and timelines,

        provide billing history.​

    The LLM decides which tool to call based on the user message; the agent maps this decision to concrete Java methods.​

    If required information is missing (for example, customer ID for billing history), the agent:

        keeps the task in IN_PROGRESS,

        explicitly asks the user for the missing field instead of silently skipping it.

TRIAGE / Out‑of‑scope handling

    When a message does not fall into technical or billing, triage assigns the task category TRIAGE.​

    The orchestrator immediately responds with a polite out‑of‑scope message explaining what the support chat can and cannot help with and marks the task as DONE.​

    This ensures that “odd” parts of multi‑topic messages are acknowledged and closed, rather than left hanging.

Manual agent orchestration

    Agent orchestration is implemented manually in Java; no agent frameworks (such as LangChain) are used, in line with the task requirements.​

    The design keeps responsibilities separated:

        orchestrator decides who should respond,

        agents decide how to handle the request within their domain.


Features

    Multi‑turn conversation with context preservation across messages.​

    Two specialised agents (Technical and Billing) plus a triage/out‑of‑scope path.​

    Manual routing logic based on task category and status.​

    LLM tool calling for billing capabilities.​

    Graceful handling of questions that cannot be answered by either agent.​

Tech Stack

    Java 17 (or newer)

    Maven for build and dependency management

    Any modern LLM (e.g. GPT, Claude, Gemini) accessed via a simple HTTP client​

    Local document search for technical documentation (vector or keyword‑based search)​

    No agentic frameworks (per task restrictions).


    Running the project
1. Configuration

Set your LLM API key as an environment variable:

bash
export LLM_API_KEY=your_api_key_here

Optionally, adjust model name / base URL in configuration (for example, application.properties or a config class).
2. Build

From the project root:

bash
mvn clean package

This will produce a JAR file in target/, for example:

text
target/support-chat-1.0.0.jar

3. Run

Start the console application:

bash
java -jar target/support-chat-1.0.0.jar

You should see a prompt similar to:

text
Multi-agent support chat. Type 'exit' to quit.
>

4. Usage

    Type natural language messages, such as:

        My integration with HubSpot keeps failing with a 500 error

        I want my billing

    Special commands:

        status – prints all current tasks with their IDs, categories and statuses,

        next – asks the orchestrator to process the next pending task,

        exit – quits the program.
