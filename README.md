# CareerGap AI

CareerGap AI is a React and Spring Boot career-development application. A candidate can save a profile, compare resume evidence with a job description, receive a readiness score and learning roadmap, track learning and skill verification, chat with a career mentor, and tailor and export resume versions.

## System architecture

```text
React 18 / TypeScript / Vite (frontend, port 5173)
             |
             | /api (Vite development proxy)
             v
Spring Boot 3.3.4 / Java 17 (backend, port 8080)
  controllers -> services -> Spring Data JPA repositories
             |
             +--> H2 in-memory (dev profile)
             +--> PostgreSQL (prod profile)
             +--> Gemini or OpenAI Chat Completions (optional)
```

The backend is organized under `backend/src/main/java/com/careergap`:

- `controller`: HTTP routes for authentication, users, analysis, learning, mentor, health, and resume building.
- `service`: application logic, deterministic skill analysis, optional LLM integration, document parsing, learning, and PDF generation.
- `dto`: request and response contracts, including validation.
- `entity` and `repository`: JPA persistence for users, skills, analyses, roadmaps, learning progress, mentor messages, and resume versions.
- `config` and `security`: stateless JWT authentication, Spring Security, and CORS.
- `exception`: API error responses.

The frontend uses React Router, an authentication context, Axios JWT interceptors, and Tailwind CSS. Vite proxies `/api` to `http://localhost:8080` during development. The frontend is in `frontend/src`.

## Feature and validation status

| Area | Current implementation and validation |
|---|---|
| Registration/login | JWT and BCrypt are implemented; registration, login, duplicate-email, password validation, and password reset workflow are covered by integration tests. |
| Protected routes and ownership | Backend routes require authentication, and user-owned resume versions and analyses are scoped to the authenticated user. Resume ownership isolation is tested. Password reset remains a development-only token response; see Security notes. |
| Profile onboarding/updates | Profile and onboarding endpoints persist user fields and related skills, projects, experiences, and certifications. Auth/onboarding integration tests cover the principal flow. |
| Resume upload/parsing | PDF, DOCX, and TXT extraction is implemented. The current integration suite does not exercise parser formats or corrupt/empty uploads. Scanned PDFs are unsupported. |
| Job description upload/parsing | Uses the same PDF/DOCX/TXT parser; parsed job text is returned to the client and is not stored separately. |
| Skill-gap analysis | Deterministic evidence-based analysis is available without an external key. Gemini and OpenAI Chat Completions are optional providers. |
| Readiness calculation | Technical and soft-skill weighted scoring is implemented; learning integration tests cover surrounding dashboard/readiness flows. |
| Learning roadmap | Roadmaps and generated learning modules persist through JPA; learning integration tests cover roadmap and module behavior. |
| Resources and practice questions | Learning resources, exercises, and practice questions are mapped in module responses and evaluated by the learning service. |
| Mock tests/scoring | Test generation, submission, scoring, and persisted attempts are implemented and covered by learning integration tests. |
| AI career mentor | Context-aware deterministic responses work without a key; Gemini/OpenAI responses are optional. Chat/history persistence is implemented. |
| Skill completion/verification | Progress, practice, and verification workflows are implemented and covered by learning integration tests. |
| Resume tailoring/PDF/versioning | Profile extraction, pending skill recommendations, tailoring, version history, PDF export, and user-isolated downloads are implemented. The dedicated integration tests cover these paths. |
| Persistent storage | JPA entities and repositories exist for the main product data. H2 is ephemeral; PostgreSQL is the configured production database. The SQL schema file is a PostgreSQL reference. |
| Frontend routes/state | Landing, registration, login, onboarding, dashboard, analyzer, roadmap, resume builder, profile, and settings routes exist. The frontend TypeScript build is the compile check; no browser E2E suite is currently configured. |
| Loading/fallback/error UI | Several pages have loading and error states. There is no application-wide React error boundary. Settings notification preferences currently remain client-side and are not persisted by a backend API. |

## Prerequisites

- JDK 17
- Apache Maven 3.8+
- Node.js 18+ and npm
- Docker Desktop (optional, for PostgreSQL through Compose)
- An AI API key only if you want LLM-generated analysis and mentor responses

## Environment variables

Backend configuration is read from Spring Boot properties and environment variables. Spring Boot does **not** automatically load a `.env` file; export the variables in your shell or configure them in your IDE/run environment. `backend/.env.example` is a reference, not an automatically sourced file.

| Variable | Default/use |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` by default. Use `dev` for in-memory H2 or `prod` for PostgreSQL. |
| `SPRING_DATASOURCE_URL` | PostgreSQL URL in the `prod` profile; override for your database. |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Database credentials. |
| `JWT_SECRET` | Base64-encoded secret used to sign JWTs. Set a private random value of at least 32 bytes for production. The built-in fallback is for local development only. |
| `JWT_EXPIRATION_MS` | JWT lifetime in milliseconds; defaults to 86,400,000 (24 hours). |
| `AI_PROVIDER` | `heuristic` by default; set to `gemini` or `openai` to enable the corresponding provider. |
| `AI_API_KEY` | Server-side API key. Leave unset for deterministic local analysis and mentor responses. |
| `AI_MODEL` | Model name; defaults to `gemini-1.5-flash`. Select a model supported by the chosen provider. |
| `VITE_API_BASE_URL` | Optional frontend API base URL; defaults to `/api`. |

For local PowerShell runs, for example:

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
$env:AI_PROVIDER = "heuristic"
$env:AI_API_KEY = ""
```

For production, set `JWT_SECRET`, database credentials, and any AI key through a secret manager or deployment environment. Do not commit real `.env` files or credentials.

## Database configuration

### H2 (development and tests)

The default `dev` profile uses an in-memory H2 database in PostgreSQL compatibility mode. Data is lost when the process exits. No database service is required:

```powershell
cd backend
$env:SPRING_PROFILES_ACTIVE = "dev"
mvn spring-boot:run
```

The H2 console is enabled at `http://localhost:8080/h2-console` in this profile. The JDBC URL is defined in `backend/src/main/resources/application-dev.yml`.

### PostgreSQL

PostgreSQL is configured by the `prod` profile. Start the included database service from the repository root:

```powershell
docker compose up -d postgres
```

Then configure the backend and run it:

```powershell
cd backend
$env:SPRING_PROFILES_ACTIVE = "prod"
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/careergap"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "postgres"
mvn spring-boot:run
```

The Compose credentials are intended for local development; replace them outside local use. Hibernate currently uses `ddl-auto: update`. `backend/src/main/resources/schema.sql` documents the PostgreSQL table layout.

### MySQL

MySQL is **not configured in this repository**: the Maven project does not include a MySQL JDBC driver or a MySQL Spring profile. `application-prod.yml` selects PostgreSQL explicitly, so setting only a MySQL JDBC URL will not work. To use MySQL, add the MySQL Connector/J runtime dependency, define a dedicated Spring profile with `com.mysql.cj.jdbc.Driver` and the MySQL dialect (or let Hibernate detect it), provide MySQL connection variables, and validate the JPA mappings against a real MySQL instance. The checked-in `schema.sql` is PostgreSQL-specific and should not be run as a MySQL schema.

## Run the application

### Backend

From the repository root:

```powershell
cd backend
mvn spring-boot:run
```

The backend listens on `http://localhost:8080`. Verify it with:

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

### Frontend

In a separate terminal:

```powershell
cd frontend
npm ci
npm run dev
```

Open `http://localhost:5173`. The Vite development server forwards API requests to the backend. To build static frontend assets:

```powershell
npm run build
```

## AI provider setup

Without an API key, the app uses its deterministic semantic evidence engine and local mentor response generator. To request Gemini responses:

```powershell
$env:AI_PROVIDER = "gemini"
$env:AI_API_KEY = "<your-gemini-api-key>"
$env:AI_MODEL = "gemini-1.5-flash"
```

For OpenAI Chat Completions, set `AI_PROVIDER=openai`, provide an OpenAI API key, and choose a chat-completions model in `AI_MODEL`. Keys are used only by the backend. If an external request fails or its response cannot be validated, analysis and mentor services log a warning and use their deterministic fallback; a valid provider key is therefore optional for local development.

## API overview

All routes except health and the listed authentication actions require a Bearer JWT.

| Method | Route | Purpose |
|---|---|---|
| `GET` | `/api/health` | Health response |
| `POST` | `/api/auth/register` | Create account |
| `POST` | `/api/auth/login` | Authenticate |
| `POST` | `/api/auth/forgot-password` | Generate a development reset token |
| `POST` | `/api/auth/reset-password` | Reset password using token |
| `GET` | `/api/auth/me` | Current authenticated profile |
| `POST` / `GET` / `PUT` | `/api/users/onboarding`, `/api/users/profile`, `/api/users/profile` | Onboarding and profile management |
| `POST` | `/api/analysis/upload-resume` | Parse and store resume text (PDF, DOCX, or TXT) |
| `POST` | `/api/analysis/upload-job` | Parse a job-description document |
| `POST` | `/api/analysis/analyze` | Skill-gap analysis and roadmap creation |
| `GET` | `/api/analysis/history`, `/api/analysis/{id}` | Analysis history and details |
| `GET` / `POST` | `/api/learning/...` | Roadmaps, practice, progress, verification, and mock tests |
| `POST` / `GET` | `/api/mentor/chat`, `/api/mentor/history` | Career mentor conversation |
| `GET` / `POST` | `/api/resume/...` | Profile extraction, tailoring, saved versions, and PDF export |

## Testing and deployment verification

Run the complete backend integration suite (including resume-builder tests):

```powershell
cd backend
mvn clean test
```

Run the frontend TypeScript and production build check:

```powershell
cd frontend
npm run build
```

For a local end-to-end smoke check:

1. Start H2-backed backend and frontend.
2. Check `/api/health`.
3. Register, sign in, and complete onboarding.
4. Upload or paste a resume and job description; run an analysis.
5. Open the roadmap, complete a module, submit a practice or mock test, and inspect the mentor route.
6. Open Resume Builder, tailor against a job description, save a version, export its PDF, and confirm the download.
7. Sign in as a second user and confirm that the first user's private resume version cannot be fetched.

`docker-compose.yml` starts only PostgreSQL; it does not build or deploy the application. This repository currently contains no production application deployment manifests. Validate production database connectivity, JWT secret configuration, AI-provider behavior, and deployment-specific CORS settings in the target environment before release.

## Security and known limitations

- Passwords are BCrypt-hashed, sessions are stateless, and authenticated API access uses JWT Bearer tokens.
- User-specific data access should always derive the user identity from the authenticated principal. Resume version downloads and analysis retrieval enforce ownership.
- Password reset currently returns its reset token in the API response and does not send email. It is suitable only for development/testing and must be replaced by a non-enumerating email delivery flow before production.
- The development profile has local fallback values. The production profile requires `JWT_SECRET`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`; set them through a secret manager or deployment environment.
- Uploaded files are bounded by the configured 10 MB multipart limit. The parser supports text-based PDFs, DOCX, and TXT only; scanned/image PDFs and legacy `.doc` files are not supported.
- Learning notification preferences are currently frontend-only. The global frontend error boundary and full browser end-to-end tests are not implemented.
