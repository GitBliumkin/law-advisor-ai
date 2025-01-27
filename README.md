# ⚖️ Law Advisor AI

**Law Advisor AI** is an in-development platform designed to help Canadian businesses identify the legal licenses they need. By gathering company data, scraping regulatory information, and leveraging AI-powered recommendations, the platform aims to simplify compliance and licensing processes.

---

## 🛠️ Features of V1.0 (In Development)
1. **Company Data Management**:
   - Store and manage business profiles (e.g., name, industry, location).
   - **In Progress**: CRUD operations for easy management of company data.

2. **Legal Data Scraping**:
   - **In Progress**: A Python-based web scraper to extract and process licensing requirements from Canadian regulatory websites.

3. **Vectorized Search**:
   - **Planned**: AI-driven embeddings to match company profiles with relevant licensing requirements.

4. **License Recommendations**:
   - **Planned**: Generate detailed licensing guidance based on company data and legal analysis.

---

## 📐 System Architecture (Planned and In Progress)
1. **Backend**:
   - **In Progress**: Java API built with Spring Boot (Maven) for managing workflows and interacting with the Postgres database.
2. **Lambda Functions**:
   - **In Progress**: Python Lambda functions for:
     - **Vector Store Interaction**: AI-powered embeddings for recommendations.
     - **Web Scraper**: Extracting and structuring legal data.
3. **Database**:
   - **Planned**: Postgres for storing company profiles, scraped data, and processed results.
4. **API Gateway**:
   - **Planned**: Routes client requests to backend services.

---

## 🚀 Tech Stack
- **Languages**: Java (Spring Boot), Python (AWS Lambda)  
- **AI**: OpenAI APIs for embeddings and natural language processing  
- **Database**: Postgres, Vector Store (for AI-powered similarity searches)  
- **Tools & Infrastructure**: Kafka, AWS Lambda, API Gateway, Docker, Terraform  
- **Web Scraping**: Scrapy (for extracting legal and regulatory data)  
- **Testing**: JUnit (backend unit tests)  

---

## 📦 Installation (Coming Soon)
The installation and deployment guide will be available after the initial development phase.

---

## 🗺️ Roadmap
1. Complete backend development using Java (Spring Boot) and Postgres integration.
2. Finalize Lambda functions for:
   - AI vectorized search.
   - Legal data scraping and processing.
3. Deploy the API Gateway for seamless client interaction.
4. Implement a licensing recommendation engine.

---

## 📫 Contact
Have questions or suggestions? Feel free to reach out:  
- **Email**: [n.bliumkin@gmail.com](mailto:n.bliumkin@gmail.com)

---

🌟 _"Simplifying compliance, one license at a time."_  
