# PingBoard

PingBoard is a Spring Boot-based endpoint monitoring service built to practice what happens after backend code is deployed.

Instead of stopping at CRUD or API delivery, this project follows the full operational loop: monitor registration, failure detection, Discord alerting, Sentry error tracking, Grafana and Loki observability, Render deployment, and GitHub Actions-based redeploy automation.

It is designed as a backend learning project focused on service operations rather than server setup alone, so the repo includes both application code and the operational documents needed to validate, observe, and respond to incidents.

## What Problem It Solves

When a small service starts failing, teams often bounce between multiple tools just to answer three simple questions:
Is the endpoint down, did the app log anything useful, and did an alert actually fire?
PingBoard compresses that loop into one local-first stack so you can register endpoints, trigger checks, receive Discord alerts, inspect logs in Grafana, and confirm exceptions in Sentry without standing up a full production platform first.

## Screenshots

![PingBoard monitor board](docs/screenshots/pingboard-dashboard.png)

![Grafana overview dashboard](docs/screenshots/grafana-overview.png)

## Features

- Register HTTP/HTTPS monitors
- List monitors and inspect the latest status
- Group monitors by environment and tags
- Trigger manual checks
- Edit monitor targets without recreating them
- Pause or resume noisy monitors without deleting them
- Resume a paused monitor and immediately run a fresh check
- Persist recent check history in H2 or PostgreSQL
- Scheduled background checks every 30 seconds
- Lightweight web dashboard at `/`
- `/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`
- Optional Sentry integration through environment variables
- Optional Slack or Discord webhook alerts for incidents and recoveries
- Basic auth for the dashboard and monitor APIs

## Stack

- Java 21
- Spring Boot 3.5
- Spring Web / Validation / Data JPA / Actuator
- H2 for local quickstart
- PostgreSQL for Docker runtime
- Prometheus metrics registry
- Grafana + Loki + Promtail local observability stack
- Sentry Spring Boot starter
- Render Blueprint for hosted deployment

## Run locally

### 1. Quick start with in-memory H2

```bash
./gradlew bootRun
```

### 2. Run with PostgreSQL

```bash
docker compose up -d postgres
set SPRING_PROFILES_ACTIVE=postgres
./gradlew bootRun
```

### 3. Full local stack with app + Postgres + Prometheus + Grafana + Loki

```bash
docker compose up --build
```

### 4. Turn on alerts

```bash
set PINGBOARD_ALERTS_ENABLED=true
set PINGBOARD_ALERTS_PROVIDER=DISCORD
set PINGBOARD_ALERTS_WEBHOOK_URL=https://discord.com/api/webhooks/...
set PINGBOARD_ALERTS_FAILURE_THRESHOLD=3
./gradlew bootRun
```

### 5. Operator login

By default, PingBoard now protects `/`, `/api/**`, and `/actuator/prometheus` with HTTP Basic auth.
The default operator account is:

```text
username: operator
password: pingboard123!
```

You can override it with environment variables:

```bash
set PINGBOARD_OPERATOR_USERNAME=ops-admin
set PINGBOARD_OPERATOR_PASSWORD=change-me
./gradlew bootRun
```

### 6. Settings template

Copy `.env.example` to `.env` and only fill the values you actually need.
If you skip Sentry and webhook values, the local stack still works.

## Deployment

PingBoard is prepared for Render-based hosting.

- Blueprint: [render.yaml](/C:/Users/ParkJaeHong/PingBoard/render.yaml)
- CI: [.github/workflows/ci.yml](/C:/Users/ParkJaeHong/PingBoard/.github/workflows/ci.yml)
- Deploy hook workflow: [.github/workflows/render-deploy.yml](/C:/Users/ParkJaeHong/PingBoard/.github/workflows/render-deploy.yml)
- Deployment checklist: [deployment-checklist.md](/C:/Users/ParkJaeHong/PingBoard/docs/operations/deployment-checklist.md)

Render notes:
- Render Blueprints support top-level `services` and `databases`.
- Health checks can be configured with `healthCheckPath`.
- Secrets can be prompted in the dashboard by marking env vars with `sync: false`.
- Render manages TLS automatically for `onrender.com` and verified custom domains.

Operational docs:
- [runbook.md](/C:/Users/ParkJaeHong/PingBoard/docs/operations/runbook.md)
- [backup-recovery.md](/C:/Users/ParkJaeHong/PingBoard/docs/operations/backup-recovery.md)
- [data-retention.md](/C:/Users/ParkJaeHong/PingBoard/docs/operations/data-retention.md)

## API

### Create a monitor

```bash
curl -X POST http://localhost:8080/api/monitors ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"OpenAI\",\"url\":\"https://example.com\",\"intervalSeconds\":60}"
```

With environment and tags:

```bash
curl -X POST http://localhost:8080/api/monitors ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Prod API\",\"url\":\"https://example.com/health\",\"intervalSeconds\":60,\"environment\":\"prod\",\"tags\":[\"critical\",\"public\"]}"
```

### List monitors

```bash
curl http://localhost:8080/api/monitors
curl http://localhost:8080/api/monitors?environment=prod
```

### Run a manual check

```bash
curl -X POST http://localhost:8080/api/monitors/1/checks
```

### Pause or resume a monitor

```bash
curl -X POST http://localhost:8080/api/monitors/1/pause
curl -X POST http://localhost:8080/api/monitors/1/resume
```

### List recent checks

```bash
curl http://localhost:8080/api/monitors/1/checks
```

### Dashboard summary

```bash
curl http://localhost:8080/api/monitors/summary
curl http://localhost:8080/api/monitors/summary?environment=staging
```

## Observability

- Health: `http://localhost:8080/actuator/health`
- Prometheus metrics: `http://localhost:8080/actuator/prometheus`
- Prometheus UI: `http://localhost:9090`
- Grafana UI: `http://localhost:3000`
- Loki API: `http://localhost:3100`
- Browser dashboard: `http://localhost:8080`

`/actuator/health` and `/actuator/info` stay public for probes.
`/actuator/prometheus` stays public so the bundled Prometheus container can scrape without extra setup.
Grafana starts with pre-provisioned Prometheus and Loki datasources plus a `PingBoard Overview` dashboard.

To enable Sentry, provide a DSN:

```bash
set SENTRY_DSN=https://<key>@<org>.ingest.sentry.io/<project>
./gradlew bootRun
```

To enable incident notifications, provide a webhook URL for Slack or Discord.
PingBoard sends one alert when a monitor crosses the failure threshold and one recovery alert when it comes back.

```bash
set PINGBOARD_ALERTS_ENABLED=true
set PINGBOARD_ALERTS_PROVIDER=SLACK
set PINGBOARD_ALERTS_WEBHOOK_URL=https://hooks.slack.com/services/...
set PINGBOARD_APP_BASE_URL=http://localhost:8080
./gradlew bootRun
```

For a quick Sentry verification after startup, trigger the built-in test endpoint with your operator credentials:

```bash
curl -X POST -u ops-admin:change-me http://localhost:8080/api/dev/sentry-test
```

## Weekend MVP ideas

- Add latency SLO alerts in Prometheus/Grafana
- Add tags or environments like `prod`, `staging`, `dev`
