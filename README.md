# CodeMentor AI

CodeMentor AI is a Spring Boot web application that gives students guided debugging feedback. It uses Groq for analysis and stores the ten most recent successful analyses in a local H2 database.

## Features

- Explanations, issues, progressive hints, test cases, and corrected code
- Eight programming languages
- Persistent local analysis history
- Responsive interface with examples, character counting, loading state, and copy-to-clipboard
- Input validation and friendly provider errors
- API credentials kept outside source control

## Run locally

Create `.env.properties` in the project root:

```properties
GROQ_API_KEY=your_key_here
```

Then run:

```bash
mvn spring-boot:run
```

Open <http://localhost:8080>.

## Test

```bash
mvn test
```

The local database is created under `data/`. Both `data/` and `.env.properties` are ignored by Git.
