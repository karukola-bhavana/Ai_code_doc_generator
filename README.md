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
