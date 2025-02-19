
# **Law Advisor API**

This document outlines the key setup steps, commands, and usage instructions for the Law Advisor API project.

## **Prerequisites**
- **Java Development Kit (JDK):** Version 11+ (ensure `JAVA_HOME` is set correctly).
- **Maven:** Version 3.6+.
- **PostgreSQL:** Ensure the database is running and credentials are configured in the `application.yml` or `application.properties` file.
- **Git:** Version control system for managing code.

---

## **Setup Instructions**
1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd law-advisor-api
   ```

2. **Configure the database:**
   - Update database credentials in `src/main/resources/application.yml` or `application.properties`.
   - Create the required database schema if it doesn't already exist.

3. **Install dependencies:**
   ```bash
   mvn clean install
   ```

4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

---

## **Code Formatting**
The project uses **Google Java Format** via the `fmt-maven-plugin`. To format the code:
```bash
mvn fmt:format
```

Alternatively, if **Spotless** is configured:
```bash
mvn spotless:apply
```

---

## **Testing**
To run the test suite:
```bash
mvn test
```

To run specific tests:
```bash
mvn -Dtest=<TestClassName> test
```

---

## **Contact**
For issues or feature requests, please open a GitHub issue or contact the maintainer.

---

Feel free to adapt this document to your project's specifics! Let me know if you'd like further refinements.