# 🤖 Multi-Agent Support System

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![AI](https://img.shields.io/badge/AI-Powered-0078D4?style=for-the-badge)

## 📖 Overview
This project implements a conversational support system in Java featuring two collaborating AI agents within a single chat: a **Technical Specialist** and a **Billing Specialist**.

---

## 🏗️ Architecture

### 🧠 Conversation Orchestration
- Central Orchestrator manages tasks
- Categories: TECHNICAL, BILLING, TRIAGE
- Status: NEW, IN_PROGRESS, DONE

### 🛠️ Technical Agent
- Uses local documentation
- No hallucination

### 💳 Billing Agent
- Tool calling
- Requires customer data

---

## 🚀 Getting Started

```bash
mvn clean package
java -jar target/support-chat-1.0.0.jar
```

---

## 💬 Example Conversation

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
