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

> i have 500 error, i want billing and i want pet rat

A 500 error indicates that the remote system failed...

> status
- [DONE] TECHNICAL
- [NEW] BILLING
- [NEW] TRIAGE

> next
BillingAgent: clarify request

> show me my billing history
BillingAgent: need ID

> 231
History:
- Feb: $49
- Mar: $49

> next
Out of scope

> make refund
Refund opened
```

---

## 🔍 What Happens

- Multiple intents → multiple tasks
- Technical handled first
- Billing requires clarification
- Missing data keeps task active
- Out-of-scope handled gracefully
