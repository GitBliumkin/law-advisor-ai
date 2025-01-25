# Law Advisor AI

## Overview
Law Advisor AI is a system designed to assist businesses in Canada by providing a list of legal licenses they need to obtain. The system receives data about a company, scrapes relevant legal information, processes the data, and provides actionable insights based on the company's profile.

---

## Features
1. **Company Data Management**:
   - Receive and store information about businesses (e.g., name, industry, location).
   - CRUD operations for company data.

2. **Legal Data Scraping**:
   - Scrape Canadian legal and regulatory websites for up-to-date licensing requirements.
   - Process and clean scraped data for accurate recommendations.

3. **Vectorized Search**:
   - Use embeddings to match company profiles with relevant legal requirements.
   - Enable similarity searches for licensing requirements across industries.

4. **Recommendations**:
   - Provide a detailed list of licenses required based on company data and legal analysis.

---

## System Architecture

### Components
1. **Java API**:
   - Acts as the main backend service.
   - Manages workflows, orchestrates tasks between Lambda functions, and interacts with the Postgres database.

2. **First Lambda (Vector Store Interaction)**:
   - Handles interaction with the vector store for embeddings-based recommendations.
   - Stores and retrieves vectorized data.

3. **Second Lambda (Scraping Service)**:
   - Scrapes legal and regulatory websites for licensing requirements.
   - Processes and structures scraped data.

4. **Postgres Database**:
   - Stores structured data about companies, scraped information, and processed results.

5. **API Gateway**:
   - Serves as the entry point for client requests and routes them to the appropriate services.

---

## Installation

### Prerequisites
- Java (17+)
- Python (3.9+)
- Docker
- AWS CLI
- Terraform or AWS CDK (for infrastructure deployment)

### Steps
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-repo/law-advisor-ai.git
   cd law-advisor-ai
   ```

2. **Set Up Java API**:
   ```bash
   cd java-api
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

3. **Set Up Lambda Functions**:
   - **First Lambda (Vector Store Interaction)**:
     ```bash
     cd lambda-vector-store
     pip install -r requirements.txt
     zip -r lambda.zip .
     aws lambda create-function ...
     ```
   - **Second Lambda (Scraping Service)**:
     ```bash
     cd lambda-scraping
     pip install -r requirements.txt
     zip -r lambda.zip .
     aws lambda create-function ...
     ```

4. **Deploy Infrastructure**:
   - Use Terraform or AWS CDK to deploy AWS resources (API Gateway, Lambda, Postgres).
   ```bash
   cd infra
   terraform init
   terraform apply
   ```

---

## Usage

### API Endpoints
1. **Add Company**:
   ```http
   POST /api/v1/companies
   {
     "name": "Example Corp",
     "industry": "Technology",
     "location": "Toronto, ON"
   }
   ```

2. **Get Company Licenses**:
   ```http
   GET /api/v1/companies/{id}/licenses
   ```

3. **Trigger Scraping**:
   ```http
   POST /api/v1/scraping
   {
     "target": "https://example-legal-site.ca"
   }
   ```

---

## Tech Stack

### Languages
- Java (Spring Boot)
- Python (AWS Lambda)

### Frameworks
- Spring Boot
- BeautifulSoup / Scrapy for web scraping

### Database
- Postgres

### Tools
- AWS Lambda
- API Gateway
- Docker
- Terraform

---

## Roadmap
1. Integrate additional legal data sources for enhanced accuracy.
2. Implement user authentication and authorization.
3. Add multi-language support for legal requirements.
4. Enhance vector search capabilities for more personalized recommendations.

---

## Contributing
We welcome contributions! Please follow these steps:
1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature-name`.
3. Commit your changes: `git commit -m 'Add your feature'`.
4. Push to the branch: `git push origin feature/your-feature-name`.
5. Open a Pull Request.

---

## License
This project is licensed under the MIT License. See the LICENSE file for details.

---

## Contact
For questions or feedback, contact us at: **support@lawadvisorai.ca**

