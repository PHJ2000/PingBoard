# PingBoard

PingBoard is a Spring Boot MVP for monitoring HTTP endpoints.

It lets you register URLs, run checks on demand, store recent check history, and expose operational metrics through Actuator and Prometheus.

## Features

- Register HTTP/HTTPS monitors
- List monitors and inspect the latest status
- Group monitors by environment and tags
- Trigger manual checks
- Pause or resume noisy monitors without deleting them
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
