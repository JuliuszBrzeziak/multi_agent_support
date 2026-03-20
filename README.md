# 🤖 Multi-Agent Support System

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![AI](https://img.shields.io/badge/AI-Powered-0078D4?style=for-the-badge)

## 📖 Overview
This project implements a conversational support system in Java featuring two collaborating AI agents within a single chat: a **Technical Specialist** and a **Billing Specialist**. The system intelligently routes each user message to the most appropriate agent, supports multi‑turn conversations, and dynamically switches agents based on context within the same conversation.

---

## 🏗️ Architecture

### 🧠 Conversation Orchestration
- **Central Orchestrator**: Maintains a queue of conversation tasks.
- **Task Properties**: 
  - `category`: `TECHNICAL`, `BILLING`, or `TRIAGE` (out‑of‑scope)
  - `status`: `NEW`, `IN_PROGRESS`, `DONE`
  - `id` and a short description.
- **Triage Mechanism**: Classifies new user messages into categories and creates tasks accordingly.
- **Task Execution**: Only one task is active at a time. The orchestrator determines which agent responds based on the task's category and status.
- **Manual Implementation**: Agent orchestration is built from scratch in Java without external frameworks (like LangChain), ensuring a clean separation of concerns: the orchestrator decides *who* responds, and the agents decide *how* to handle the request.

### 🛠️ Technical Agent (Technical Specialist)
- Answers technical questions using a focused set of local documentation files.
- **Workflow**:
  1. Retrieves the most relevant documentation snippets using local search (e.g., semantic/vector or keyword-based).
  2. Passes only the retrieved snippets as context to the LLM.
  3. Generates an answer strictly grounded in the provided documents.
- If information is missing, it will either ask the user for clarification or explicitly state that the information is unavailable (no guessing/hallucination).

### 💳 Billing Agent (Billing Specialist)
- Handles billing‑related inquiries and requests via **LLM tool calling**.
- **Backend Capabilities**:
  - Confirm customer plans and pricing.
  - Open refund cases.
  - Explain refund policies and timelines.
  - Provide billing history.
- The LLM dynamically decides which tool to call based on the user's message, and the agent maps this to concrete Java methods.
- **Context Handling**: If required data (e.g., customer ID) is missing, the agent keeps the task `IN_PROGRESS` and explicitly asks the user for the missing details.

### 🚪 Triage / Out‑of‑Scope Handling
- If a message does not pertain to technical or billing support, it is assigned the `TRIAGE` category.
- The orchestrator responds immediately with a polite out‑of‑scope message detailing what the system can and cannot do, then marks the task as `DONE`.
- This ensures unrelated multi-topic messages are gracefully acknowledged and closed.

---

## ✨ Features
- **Multi-Turn Conversations**: Context preservation across multiple messages.
- **Specialized Agents**: Dual-agent system (Technical & Billing) plus a dedicated out‑of‑scope pathway.
- **Manual Routing**: Custom logic based on task categories and statuses.
- **LLM Tool Calling**: Advanced tool integration for executing billing actions.
- **Graceful Fallbacks**: Safe handling of unanswerable or ambiguous questions.

---

## 💻 Tech Stack
- **Java 17+**
- **Maven** (Build and dependency management)
- **Modern LLM** (GPT, Claude, Gemini, etc.) accessed via a simple HTTP client.
- **Local Document Search** (Vector or keyword‑based search for technical docs).
- *No agentic frameworks used.*

---

## 🚀 Getting Started

### 1. Configuration
Set your LLM API key as an environment variable:
```bash
export LLM_API_KEY=your_api_key_here
```
*(Optionally, adjust the model name or base URL in your configuration file, e.g., `application.properties`)*

### 2. Build
From the project root directory, run:
```bash
mvn clean package
```
This will generate a JAR file in the `target/` directory, for example: `target/support-chat-1.0.0.jar`.

### 3. Run
Start the console application:
```bash
java -jar target/support-chat-1.0.0.jar
```
You should see a prompt similar to:
```text
Multi-agent support chat. Type 'exit' to quit.
>
```

### 4. Usage
Type natural language messages, for example:
- *"My integration with HubSpot keeps failing with a 500 error"*
- *"I want my billing history"*

**Special Commands:**
- `status` – Prints all current tasks with their IDs, categories, and statuses.
- `next` – Asks the orchestrator to process the next pending task.
- `exit` – Quits the application.
