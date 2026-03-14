# PingBoard

PingBoard is a Spring Boot MVP for monitoring HTTP endpoints.

It lets you register URLs, run checks on demand, store recent check history, and expose operational metrics through Actuator and Prometheus.

## Features

- Register HTTP/HTTPS monitors
- List monitors and inspect the latest status
- Trigger manual checks
- Pause or resume noisy monitors without deleting them
- Persist recent check history in H2 or PostgreSQL
- Scheduled background checks every 30 seconds
- Lightweight web dashboard at `/`
- `/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`
- Optional Sentry integration through environment variables

## Stack

- Java 21
- Spring Boot 3.5
- Spring Web / Validation / Data JPA / Actuator
- H2 for local quickstart
- PostgreSQL for Docker runtime
- Prometheus metrics registry
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

### 3. Full local stack with app + Postgres + Prometheus

```bash
docker compose up --build
```

## API

### Create a monitor

```bash
curl -X POST http://localhost:8080/api/monitors ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"OpenAI\",\"url\":\"https://example.com\",\"intervalSeconds\":60}"
```

### List monitors

```bash
curl http://localhost:8080/api/monitors
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
```

## Observability

- Health: `http://localhost:8080/actuator/health`
- Prometheus metrics: `http://localhost:8080/actuator/prometheus`
- Prometheus UI: `http://localhost:9090`
- Browser dashboard: `http://localhost:8080`

To enable Sentry, provide a DSN:

```bash
set SENTRY_DSN=https://<key>@<org>.ingest.sentry.io/<project>
./gradlew bootRun
```

## Weekend MVP ideas

- Add pause/resume endpoints for monitors
- Add latency SLO alerts in Prometheus/Grafana
- Add Slack or Discord notifications for repeated failures
- Add tags or environments like `prod`, `staging`, `dev`
