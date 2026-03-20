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

## Technical documentation files

### Example Conversation

Below is a real example showing how the system handles multiple topics in one conversation, separates them into tasks, and switches between agents when needed.

```
Multi-agent support chat. Type 'exit' to quit.
> i have 500 error and i want billing and i want my rat
A 500 error indicates that the remote system (for example, HubSpot) failed to process your request. This is usually a temporary issue on their side, but it can also be triggered by invalid payloads. 

When you see a 500, you should log the full request body and correlation ID, then try again with a simple test payload. If the error persists, check the third-party status page and configure exponential backoff retries instead of retrying in a tight loop.
> status
Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [NEW] BILLING (id=2): i want billing
- [NEW] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want my rat

> next
BillingAgent: I see you mentioned a billing topic, but I'm not sure what you want me to do.
I can:
- confirm your current plan and pricing,
- open a refund case,
- explain our refund policy,
- show your recent billing history.

Could you please clarify what exactly you need help with?

> status
Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [DONE] BILLING (id=2): i want billing
- [NEW] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want my rat

> what
I’m sorry, but I cannot assist with that request.
Please contact our general support team.

> exit
juliuszbrzeziak@MacBook-Pro-Juliusz multi_agent_support %  cd /Users/juliuszbrzeziak/Documents/INK/multi_agent_support ; /usr/b
in/env /opt/homebrew/Cellar/openjdk@17/17.0.18/libexec/openjdk.jdk/Contents/Home/bin/java @/var/folders/x0/x_fr7c5n5777bx641wb2
_stw0000gn/T/cp_8s8day65uhi356efwiv7s4z1k.argfile com.juliusz.support.App 
Multi-agent support chat. Type 'exit' to quit.
> i have 500 error, i want billing and i want pet rat
A 500 error indicates that the remote system (for example, HubSpot) failed to process your request. This is usually a temporary issue on their side, but it can also be triggered by invalid payloads.

When you see a 500, log the full request body and correlation ID, then try again with a simple test payload. If the error persists, check the third‑party status page and configure exponential backoff retries instead of retrying in a tight loop.
> status
Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [NEW] BILLING (id=2): i want billing
- [NEW] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want pet rat

> next
BillingAgent: I see you mentioned a billing topic, but I'm not sure what you want me to do.
I can:
- confirm your current plan and pricing,
- open a refund case,
- explain our refund policy,
- show your recent billing history.

Could you please clarify what exactly you need help with?

> status
Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [IN_PROGRESS] BILLING (id=2): i want billing
- [NEW] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want pet rat

> show me my billing history
BillingAgent: I still need your customer ID to show your billing history.
Please provide the ID (for example: 123-456-789).

> status
Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [IN_PROGRESS] BILLING (id=2): i want billing
- [NEW] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want pet rat

> 231
Here is the billing history for customer 231:
- February 1, 2025: $49.00 (Pro plan)
- March 1, 2025: $49.00 (Pro plan)
- April 1, 2025: $49.00 (Pro plan)
> status
Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [DONE] BILLING (id=2): i want billing
- [NEW] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want pet rat

> next
I’m sorry, but I cannot assist with that request.
Please contact our general support team.

> status
Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [DONE] BILLING (id=2): i want billing
- [DONE] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want pet rat

> make refund
A refund case has been opened for you. Our billing team will review your request within 3–5 business days. You will be notified once the review is complete.
> status
Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [DONE] BILLING (id=2): i want billing
- [DONE] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want pet rat
- [DONE] BILLING (id=4): make refund

> exit
juliuszbrzeziak@MacBook-Pro-Juliusz multi_agent_support % mvn clean package
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.126 s
[INFO] Finished at: 2026-03-20T04:47:07+01:00
[INFO] ------------------------------------------------------------------------
[ERROR] The goal you specified requires a project to execute but there is no POM in this directory (/Users/juliuszbrzeziak/Documents/INK/multi_agent_support). Please verify you invoked Maven from the correct directory. -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MissingProjectException
juliuszbrzeziak@MacBook-Pro-Juliusz multi_agent_support % ls
multi-agent-support     README.md
juliuszbrzeziak@MacBook-Pro-Juliusz multi_agent_support % cd multi-agent-support 
juliuszbrzeziak@MacBook-Pro-Juliusz multi-agent-support % ls
pom.xml src     target
juliuszbrzeziak@MacBook-Pro-Juliusz multi-agent-support % mvn clean package
[INFO] Scanning for projects...
[INFO] 
[INFO] --------------< com.juliusz.support:multi-agent-support >---------------
[INFO] Building multi-agent-support 1.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- clean:3.1.0:clean (default-clean) @ multi-agent-support ---
[INFO] Deleting /Users/juliuszbrzeziak/Documents/INK/multi_agent_support/multi-agent-support/target
[INFO] 
[INFO] --- resources:3.0.2:resources (default-resources) @ multi-agent-support ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 4 resources
[INFO] 
[INFO] --- compiler:3.8.0:compile (default-compile) @ multi-agent-support ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 12 source files to /Users/juliuszbrzeziak/Documents/INK/multi_agent_support/multi-agent-support/target/classes
[INFO] 
[INFO] --- resources:3.0.2:testResources (default-testResources) @ multi-agent-support ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /Users/juliuszbrzeziak/Documents/INK/multi_agent_support/multi-agent-support/src/test/resources
[INFO] 
[INFO] --- compiler:3.8.0:testCompile (default-testCompile) @ multi-agent-support ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 1 source file to /Users/juliuszbrzeziak/Documents/INK/multi_agent_support/multi-agent-support/target/test-classes
[INFO] 
[INFO] --- surefire:2.22.1:test (default-test) @ multi-agent-support ---
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.juliusz.support.AppTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.016 s - in com.juliusz.support.AppTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] 
[INFO] --- jar:3.3.0:jar (default-jar) @ multi-agent-support ---
[INFO] Building jar: /Users/juliuszbrzeziak/Documents/INK/multi_agent_support/multi-agent-support/target/multi-agent-support-1.0-SNAPSHOT.jar
[INFO] 
[INFO] --- assembly:3.6.0:single (default) @ multi-agent-support ---
[INFO] Building jar: /Users/juliuszbrzeziak/Documents/INK/multi_agent_support/multi-agent-support/target/multi-agent-support-1.0-SNAPSHOT-jar-with-dependencies.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  4.965 s
[INFO] Finished at: 2026-03-20T04:47:30+01:00
[INFO] ------------------------------------------------------------------------
juliuszbrzeziak@MacBook-Pro-Juliusz multi-agent-support % ls        
pom.xml src     target
juliuszbrzeziak@MacBook-Pro-Juliusz multi-agent-support % java -jar target/multi-agent-support-1.0-SNAPSHOT.jar 
 cd /Users/juliuszbrzeziak/Documents/INK/multi_agent_support ; /usr/bin/env /opt/homebrew/Cellar/openjdk@17/17.0.18/libexec/openjdk.jdk/Contents/Home/bin/java @/var/folders/x0/x_fr7c5n5777bx641wb2_stw0000gn/T/cp_8s8day65uhi356efwiv7s4z1k.argfile com.juliusz.support.App 
juliuszbrzeziak@MacBook-Pro-Juliusz multi-agent-support %  cd /Users/juliuszbrzeziak/Documents/INK/multi_agent_support ; /usr/b
in/env /opt/homebrew/Cellar/openjdk@17/17.0.18/libexec/openjdk.jdk/Contents/Home/bin/java @/var/folders/x0/x_fr7c5n5777bx641wb2
_stw0000gn/T/cp_8s8day65uhi356efwiv7s4z1k.argfile com.juliusz.support.App 
Multi-agent support chat. Type 'exit' to quit.
> i have 500 error and i want billing and i want to pet my rat
TechnicalAgent: A 500 error indicates that the remote system (for example, HubSpot) failed to process your request. This is usually a temporary issue on their side, but it can also be triggered by invalid payloads.

When you see a 500, log the full request body and correlation ID, then try again with a simple test payload. If the error persists, check the third‑party status page and configure exponential backoff retries instead of retrying in a tight loop.
> status
SupportAgent: Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [NEW] BILLING (id=2): i want billing
- [NEW] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want to pet my rat

> next
BillingAgent: I see you mentioned a billing topic, but I'm not sure what you want me to do.
I can:
- confirm your current plan and pricing,
- open a refund case,
- explain our refund policy,
- show your recent billing history.

Could you please clarify what exactly you need help with?

> status     
SupportAgent: Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [IN_PROGRESS] BILLING (id=2): i want billing
- [NEW] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want to pet my rat

> show recent billing
BillingAgent: I still need your customer ID to show your billing history.
Please provide the ID (for example: 123-456-789).

> status
SupportAgent: Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [IN_PROGRESS] BILLING (id=2): i want billing
- [NEW] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want to pet my rat

> i dont know
BillingAgent: I still need your customer ID to show your billing history.
Please provide the ID (for example: 123-456-789).

> status
SupportAgent: Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [IN_PROGRESS] BILLING (id=2): i want billing
- [NEW] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want to pet my rat

> 1234
BillingAgent: Here is the billing history for customer 1234:

- February 1, 2025: $49.00 (Pro plan)
- March 1, 2025: $49.00 (Pro plan)
- April 1, 2025: $49.00 (Pro plan)
> status
SupportAgent: Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [DONE] BILLING (id=2): i want billing
- [NEW] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want to pet my rat

> next
SupportAgent: This part of your request is outside what this support chat can help with:
- i want to pet my rat

I can only assist with technical issues and billing questions.
For anything else, please contact our general support team.

> status
SupportAgent: Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [DONE] BILLING (id=2): i want billing
- [DONE] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want to pet my rat

> i want to make refund
BillingAgent: I've submitted your refund request. Our billing team will review it within 3–5 business days and update you with the outcome. If you have any additional information or questions, please let me know.
> status
SupportAgent: Current tasks:
- [DONE] TECHNICAL (id=1): 500 error
- [DONE] BILLING (id=2): i want billing
- [DONE] TRIAGE (triage pending / out-of-scope candidate) (id=3): i want to pet my rat
- [DONE] BILLING (id=4): want to make refund

> exit
```


What Happens in This Scenario

This conversation demonstrates several important behaviors of the system:

    The first message contains three different intents, so the system creates separate tasks.

    The Technical Agent handles the 500 error request first and provides troubleshooting guidance.

    The Billing Agent does not guess what the user wants and asks for clarification.

    When the user asks for billing history, the agent detects that the customer ID is required and keeps the task in progress.

    After receiving the ID, the Billing Agent completes the request and marks the task as done.

    The pet rat request is treated as out of scope, so it is routed through the triage path and closed politely.

    Later, the user creates a new billing task with make refund, and the system handles it independently from the previous billing history request.