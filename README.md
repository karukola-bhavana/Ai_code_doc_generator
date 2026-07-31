# 🚀 AI Code Documentation Generator

A full-stack Java Spring Boot web application that analyzes Java repositories using **JavaParser AST** and automatically generates comprehensive documentation using **AI models (Groq Llama 3.3 / Google Gemini)**.

---

## ✨ Features

- 🔗 **GitHub Repository Import**: Paste any public GitHub repo URL to fetch source files.
- 🔍 **AST Code Analysis**: Parses classes, interfaces, Spring `@RestController` annotations, REST endpoints, fields, and method signatures.
- 🤖 **Multi-Provider AI Documentation**:
  - **`README.md`**: Project overview, installation, architecture, and feature guide.
  - **`API Documentation`**: REST API endpoints, HTTP methods, parameters, and status codes.
  - **`System Architecture`**: Technical stack summary and interactive **Mermaid.js sequence diagrams**.
  - **`Code Comments & Refactoring`**: Javadoc comment standards, thread-safety tips, and code smell detection.
- 🎨 **Glassmorphism Web UI**: Dark mode web interface with live pipeline progress indicators.
- 📝 **Live Markdown Editor**: Interactive editor with real-time preview powered by Marked.js.
- 📥 **One-Click Download**: Export and download generated `README.md` directly.

---

## 🛠️ Architecture & Pipeline Flow

```
User (Paste Repo URL)
        │
        ▼
Spring Boot Backend (http://localhost:8080)
        │
        ┌───────────────────────────┼───────────────────────────┐
        ▼                           ▼                           ▼
  GitHub Service            Java Code Parser              AI Engine Service
(Fetch Metadata & Files)   (AST & REST Inspection)         (Groq / Gemini API)
        │                           │                           │
        └───────────────────────────┼───────────────────────────┘
                                     ▼
                    Documentation Viewer & Editor
                       (Download README.md)
```

---

## 💻 Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 17+, Spring Boot 3.5.4, Maven |
| **Frontend** | HTML5, Vanilla CSS3 (Glassmorphism design tokens), JavaScript (ES6+), Marked.js |
| **AST Parsing** | JavaParser (`javaparser-symbol-solver-core`) |
| **GitHub Integration** | Kohsuke GitHub API (`github-api`) |
| **AI Integrations** | Groq Cloud API (`llama-3.3-70b-versatile`), Google GenAI SDK (`gemini-2.0-flash`) |

---

## ⚙️ Prerequisites

- **Java Development Kit (JDK)**: Java 17 or higher
- **Maven**: (Included via `mvnw.cmd` / `mvnw` wrapper)
- **API Key**: Groq API key (`gsk_...`) or Google AI Studio API key (`AIzaSy...`)

---

## 🚀 Quick Start & How to Run

### 1. Clone the Repository

```bash
git clone https://github.com/bhavana/AI_Code_Doc_Generator.git
cd AI_Code_Doc_Generator
```

### 2. Configure Your API Key

Open `src/main/resources/application.properties` and add your Groq or Gemini API key:

```properties
# Using Groq API Key (Recommended for high speed)
gemini.api.key=gsk_YOUR_GROQ_API_KEY_HERE
gemini.model=llama-3.3-70b-versatile

# Or Using Google AI Studio Gemini API Key
# gemini.api.key=AIzaSy_YOUR_GEMINI_KEY_HERE
# gemini.model=gemini-2.0-flash
```

### 3. Run the Application

**Windows (Command Prompt / PowerShell):**

```cmd
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**

```bash
./mvnw spring-boot:run
```

### 4. Open in Browser

Navigate to [http://localhost:8080/](http://localhost:8080/) in your browser.

---

## 🧪 Running Unit Tests

Run all unit tests using Maven:

```cmd
.\mvnw.cmd test
```

---

## 📁 Project Structure

```
AI_Code_Doc_Generator/
├── src/
│   ├── main/
│   │   ├── java/com/bhavana/aidoc/
│   │   │   ├── config/          # Spring Beans & Client Configurations
│   │   │   ├── controller/      # REST API & Page Routing Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Global Exception Handler
│   │   │   ├── github/          # GitHub Repo Fetcher & URL Parser
│   │   │   ├── parser/          # JavaParser AST Code Parser
│   │   │   ├── prompt/          # Structured AI Prompt Templates
│   │   │   └── service/         # Documentation & AI Orchestrator
│   │   └── resources/
│   │       ├── static/          # Web UI (index.html, css/style.css, js/app.js)
│   │       └── application.properties
│   └── test/                    # Unit Tests
├── pom.xml
└── README.md
```

---

## 🤝 Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request for bug fixes, new features, or documentation improvements.

## 📄 License

This project is open source and available for personal and educational use.
