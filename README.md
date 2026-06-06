# E-Commerce Law Information Retrieval System - Backend (Spring Boot)

This is the backend repository for the **Information Retrieval System for the Law on Electronic Transactions**. The system delivers an intelligent legal query platform powered by **Ontology** and **Knowledge Graph** architectures. It seamlessly manages structured legal documents, generates automatic knowledge networks, and processes natural language processing (NLP) inputs to produce accurate, referenced legal insights.

## 🛠 Tech Stack

* **Core Framework:** Spring Boot (Java 17).
* **Database:** MySQL (Structured storage for legal hierarchies, concepts, and triplets).
* **Authentication & Authorization:** Clerk with OAuth2 integration (JWT Token validation).
* **Natural Language Processing (NLP):** VNCoreNLP (Tokenization, POS tagging, NER, and Dependency Parsing).
* **Reasoning Engine:** Drools Rule Engine (Automating legal condition evaluation and validation rules).
* **Retrieval Architecture:** Retrieval-Augmented Generation (RAG) coupled with Graph Database matching models.

## ✨ Core Features

1. **Legal Knowledge Graph Engineering:**
    * Automated parsing of legal documents (Laws, Decrees, Circulars) into structured Hierarchies (Chapter - Article - Clause - Point) using Regex.
    * Dynamic generation of RDF-style triplets: `Subject – Relation – Object`.
2. **Semantic Search & NLP Processing:**
    * User query preprocessing utilizing LLM orchestration to sanitize typos, syntax, and grammatical structures.
    * Mapping query semantics to semantic triplets for precise Subgraph Matching within the Knowledge Graph.
3. **Rule-Based Automated Legal Reasoning:**
    * Leveraging Drools Rule Engine to evaluate compound legal conditions, compliance requirements, and validation criteria.
4. **Admin Management Middleware:**
    * Dedicated endpoints to fine-tune, update, and manage Ontology Concepts and Relations.
    * Granular control over specific legal text blocks and triplet associations.

## 📂 Project Architecture

The codebase adheres to clean architectural principles and standard Spring Boot multi-layered structuring:
* `controller`: Exposes secure REST endpoints to interact with the frontend client.
* `service`: Core business logic encapsulation, orchestrating NLP pipelines and Rule Engines.
* `repository`: Handles database operations via Spring Data JPA interfaces.
* `model/entity`: Defines Object-Relational Mapping (ORM) classes for database schemas (`Laws`, `Chapters`, `Articles`, `Concepts`, `Triplets`, etc.).
* `config`: Global configurations management (Security rules, Clerk hooks, Drools instance provisioning).

## 🚀 Key API Reference

| Method | Endpoint | Action |
| :--- | :--- | :--- |
| **GET** | `/api/search` | Processes NLP queries and returns accurate answers with legal source references. |
| **POST** | `/api/load/document` | Uploads raw legal texts to execute regex-driven graph structural rendering. |
| **GET / PUT** | `/api/ontology/concept` | Manages or updates core entities in the structural domain models. |
| **POST** | `/api/article/{id}` | Manages manual target entry points for precision triplet insertion overrides. |

## ⚙️ Getting Started


1. **Prerequisites:** Ensure JDK 17+ and MySQL 8.0+ are installed locally.
2. **Configuration:** Configure your database credentials and Clerk API backend secret configurations inside the `application.yml` profile directory.
3. **Execution:** Run the application locally via the Maven wrapper:
```bash
   ./mvnw spring-boot:run 
```
## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author
*   **Nguyen Ngoc Son** - Information Technology Graduate, Saigon University (Class of 2025).


