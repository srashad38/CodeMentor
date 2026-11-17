# CodeMentor AI

A Java 21 / Spring Boot learning application that turns a code snippet into guided debugging feedback. It combines Groq-generated explanations with local static checks, persists analyses in H2 through Spring Data JPA, and lets learners track their progress in a Thymeleaf web interface.

## What it does

- Supports Java, Python, JavaScript, TypeScript, C++, C#, Go, and Rust.
- Returns a summary, issues, progressive hints, suggested test cases, an explanation, and corrected code.
- Adds local static findings alongside AI-generated feedback.
- Saves successful analyses and displays the ten most recent in the history sidebar. Older records remain in the database.
- Lets learners mark an analysis as solved or needing practice and records hints viewed.
- Shows learning analytics: submission counts, progress totals, mistake-category counts, and a recommendation associated with the most common category.
- Validates blank, unsupported, and oversized submissions (20,000-character limit).

## Run locally

Requirements: **JDK 21+**, Maven 3.9+ (or the included Maven wrapper), and a Groq API key for live analysis. Dependencies are downloaded on the first build.

```sh
git clone https://github.com/srashad38/CodeMentor.git
cd CodeMentor
```

Create `.env.properties` in the project root:

```properties
GROQ_API_KEY=your_key_here
```

Keep the real key private. This file and the local `data/` directory are excluded by `.gitignore`.

```sh
mvn spring-boot:run
```

Open <http://localhost:8080>. Alternatively, use `./mvnw spring-boot:run` on macOS/Linux or `mvnw.cmd spring-boot:run` on Windows.

The configured provider is Groq and the configured model is `openai/gpt-oss-20b`; these settings are in `src/main/resources/application.properties`. Provider availability, limits, and usage charges depend on your Groq account. A missing API key prevents analysis, not local source inspection or the mocked unit tests.

## Try the workflow

1. Choose a language and enter a short, non-sensitive example with a bug.
2. Request analysis, then work through the hints before checking the corrected code.
3. Reopen the saved analysis from history and mark your progress.
4. Visit `/analytics` to see the accumulated category and progress summaries.

AI output can be wrong. The suggested test cases are text for the learner to review and run separately; this app is not a code-execution sandbox and does not prove that a correction works.

## Architecture

```text
Browser / Thymeleaf
  -> CodeAnalysisController       Pages, analysis submission, history, progress
  -> CodeAnalysisService          Validation, orchestration, learning analytics
     -> AiService                 Groq HTTP request and response parsing
     -> StaticAnalysisService     Local checks
     -> CodeSubmissionRepository  Spring Data JPA persistence
        -> H2                     Local file-backed storage
```

Source is under `src/main/java/com/codementor/`; templates, styles, and browser behavior are under `src/main/resources/`.

## Verification

```sh
mvn verify
```

The current unit tests cover blank input, unsupported languages, oversized input, and successful analysis persistence with a mocked AI provider. They do not validate live Groq responses, browser interaction, or the full analytics workflow. No live-provider success or production readiness is implied by a passing unit test run.

## Data and deployment limits

Submitted code is sent to Groq for analysis and successful submissions are saved locally under `data/`. Use fictional or public example code, not secrets, customer data, or private employer code. Never commit `.env.properties` or database files.

This is a local portfolio and learning application, not a multi-user production service. It has no account authentication or per-user isolation. Do not expose it publicly as-is. The analytics describe saved practice activity and AI-assigned categories, not a validated assessment of a learner's ability.

## Next steps

- Add controller, persistence, and analytics tests.
- Add automated CI and browser smoke tests.
- Add user isolation, authentication, and safer operational error handling before any hosted multi-user deployment.
