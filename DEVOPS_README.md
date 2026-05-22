# GitLab CI/CD Pipeline Report

## Overview

This pipeline is structured around four sequential stages: **build**, **static_analysis**, **test**, and **deploy**. Each stage runs jobs for both the backend (Gradle/Spring Boot) and the frontend (Angular/Node), ensuring a clean separation of concerns throughout the delivery process.

---

## Stage Responsibilities and Tools Used

### 1. `build`

This stage compiles both applications without running tests or quality checks, ensuring the codebase is syntactically valid and all dependencies can be resolved before heavier stages begin.

| Job | Working Directory | Image | Responsibility |
|---|---|---|---|
| `build:backend` | `src/backend` | `gradle:8-jdk21` | Runs `./gradlew build -x test -x check` to compile the Java source and resolve dependencies |
| `build:frontend` | `src/frontend` | `node:trixie` | Runs `npm ci` to install exact dependencies and `npx ng build` to compile the Angular app |

**Tools:** Gradle 8, JDK 21, Node.js (Debian Trixie), Angular CLI (`ng`)

---

### 2. `static_analysis`

This stage enforces code quality and style rules without executing the application. It acts as a gate before testing to catch issues early and cheaply.

| Job | Working Directory | Image | Responsibility |
|---|---|---|---|
| `static_analysis:backend` | `src/backend` | `gradle:8-jdk21` | Runs `./gradlew check -x test` to execute Checkstyle, PMD, or similar Gradle quality plugins |
| `static_analysis:frontend` | `src/frontend` | `node:trixie` | Runs `npx ng lint` to enforce ESLint rules on the Angular codebase |

**Tools:** Gradle `check` task (static analysis plugins), Angular ESLint

---

### 3. `dependency_scan` (NEW)

#### Backend

We use [Trivy](https://github.com/aquasecurity/trivy) to scan the backend dependencies for known vulnerabilities. Trivy analyzes the Gradle dependency tree and checks it against vulnerability databases. The pipeline will fail if any vulnerabilities of any severity are found, ensuring that we address security issues in our dependencies before proceeding to testing and deployment.

#### Frontend

We use the command `npm audit` to scan for vulnerabilities in the frontend dependencies. The pipeline will fail if any vulnerabilities of any severity are found, ensuring that we address security issues in our dependencies before proceeding to testing and deployment.

---

### 4. `test`

#### Frontend and backend

This stage runs the full test suites and collects coverage data for both applications.

| Job | Working Directory | Image | Responsibility |
|---|---|---|---|
| `test:backend` | `src/backend` | `gradle:8-jdk21` | Runs `./gradlew test jacocoTestReport` against a live MySQL instance |
| `test:frontend` | `src/frontend` | `node:trixie` | Runs `npx ng test` to execute the Angular unit test suite |

**Tools:** JUnit (via Gradle), JaCoCo, Angular/Karma test runner, MySQL 8.0 (as a service)

The backend test job also uses a **Gradle cache** (`backend/.gradle/`) keyed by `backend-$CI_COMMIT_REF_SLUG` to speed up repeated runs on the same branch.

#### End-to-end (NEW)

We do in the pipeline end-to-end testing with `Cypress`. The `test:e2e` job runs after the unit tests and assumes that the application can be started in a test environment. It uses the `cypress/included:12.0.0` Docker image, which includes both Cypress and its dependencies. This job is only triggered on the branches `main` and `dev` to ensure that end-to-end tests are run on stable code.

---

### 5. `deploy`

This stage produces the final build artifacts and simulates deployment. In its current state, it verifies that the artifacts are present and ready for a downstream deployment step.

| Job | Working Directory | Image | Responsibility |
|---|---|---|---|
| `deploy:backend` | `src/backend` | `gradle:8-jdk21` | Runs `./gradlew bootJar` to produce the Spring Boot executable JAR under `build/libs/` |
| `deploy:frontend` | `src/frontend` | `node:trixie` | Runs `npx ng build --configuration production` to produce optimized assets under `dist/` |

**Tools:** Spring Boot Gradle plugin (`bootJar`), Angular CLI production build

This job is only triggered on the branches `main` and `dev` to ensure that end-to-end tests are run on stable code.

---

## How Coverage Is Collected

Coverage is extracted by GitLab CI using regex patterns defined in the `coverage` field of each test job. GitLab scans the job logs for a line matching the pattern and parses the percentage out of it.

### Backend — JaCoCo

The backend uses **JaCoCo**, which prints a summary table at the end of the `jacocoTestReport` task.

**Format of the test coverage report**

Test Coverage:
    - Class Coverage: X%
    - Method Coverage: X%
    - Branch Coverage: X%
    - Line Coverage: X%
    - Instruction Coverage: X%
    - Complexity Coverage: X%

### Frontend — Angular/Istanbul

The Angular test runner (Karma + Istanbul) prints a coverage summary.

**Format of the test coverage report**

File               | % Stmts | % Branch | % Funcs | % Lines | Uncovered Line #s 
-------------------|---------|----------|---------|---------|-------------------

---

## Thresholds on test coverage (NEW)

We set a minimum threshold for both backend and frontend test coverage. If the coverage falls below this threshold, the pipeline will fail, enforcing a standard of quality.

---

## Trigger Choice and Reasoning

```yaml
workflow:
  rules:
    - if: $CI_PIPELINE_SOURCE == "merge_request_event"
    - if: $CI_COMMIT_BRANCH == "main"
    - if: $CI_COMMIT_BRANCH == "dev"
```

For all jobs except the deployement and the end-to-end testing, the pipeline is configured to trigger **only on merge request events**, and on the branches `main` and `dev`. All jobs without exception will me triggered on `main` and `dev` branches. This ensure to have minimum quality on the features branches, and to have a good quality on the `main` and `dev` branches.

The pipeline is configured to trigger **only on merge request events**. This is a deliberate and common strategy for the following reasons:

- **Focused validation:** The pipeline runs exactly when code review is happening, providing the reviewer and author with immediate feedback on build, quality, tests, and coverage.
- **No redundant runs:** Without this rule, GitLab would trigger pipelines on both the branch push *and* the merge request, causing the same work to be done twice. Restricting to `merge_request_event` eliminates that duplication.
- **Cost efficiency:** Runner minutes are only consumed when a human action (opening or updating an MR) warrants it, rather than on every commit push.
- **Clean MR context:** Variables like `$CI_MERGE_REQUEST_IID` and diff-aware features are only available in merge request pipelines, making this the right context for quality gates.

> **Note:** As currently configured, no pipeline will run on direct pushes to `main`/`master` or on tag events. If post-merge deployment or release pipelines are needed, additional `workflow.rules` entries (e.g., `if: $CI_COMMIT_BRANCH == "main"`) should be added.

---

## Required CI Variables, Images, and Runner Considerations

### Docker Images

| Image | Used By |
|---|---|
| `gradle:8-jdk21` | All backend jobs |
| `node:trixie` | All frontend jobs |
| `mysql:8.0` | Service dependency of `test:backend` |
| `cypress/included:latest` | End-to-end |

These images are pulled from Docker Hub. Runners must have **internet access** or a configured **registry mirror/proxy** to resolve them.

### CI Variables (defined inline in the pipeline)

These variables are declared directly in the `test:backend` job and do not need to be set in the GitLab project settings:

| Variable | Value | Purpose |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | `root` | Root password for the MySQL service container |
| `MYSQL_DATABASE` | `mydb` | Database name created on startup |
| `MYSQL_USER` | `myuser` | Application database user |
| `MYSQL_PASSWORD` | `pwd` | Password for the application user |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql:3306/mydb?...` | JDBC URL pointing to the `mysql` service alias |
| `SPRING_DATASOURCE_USERNAME` | `myuser` | Spring Boot datasource username |
| `SPRING_DATASOURCE_PASSWORD` | `pwd` | Spring Boot datasource password |

### Runner Requirements

- Runners must support the **Docker executor** to use the `image:` and `services:` keywords.
- The `services:` block in `test:backend` requires Docker-in-Docker or a runner configured to allow service containers (standard with the Docker executor).
- No specific tags are defined on any job, so any available runner with a Docker executor will be eligible to pick up jobs.