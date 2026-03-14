# PingBoard Interview Script

## 1. One-minute introduction

PingBoard is a Spring Boot-based endpoint monitoring service that I built to practice not only backend API development, but also the operational lifecycle after deployment.

The core idea was to move beyond "the API works locally" and verify a full production-like loop: registering monitor targets, detecting failures, sending Discord alerts, capturing exceptions in Sentry, observing metrics and logs with Prometheus, Grafana, and Loki, and deploying the service to Render with GitHub Actions-based redeploy automation.

Through this project, I learned the difference between simply deploying a backend and actually operating a service with alerting, observability, incident response, and deployment readiness in mind.

## 2. Why I built it

Most personal backend projects stop at implementing features or basic CRUD APIs.

I wanted a project that would help me understand what happens after deployment:

- how to detect a broken endpoint
- how to receive alerts when something fails
- how to inspect logs and metrics
- how to trace exceptions
- how to validate that the deployed service is healthy

PingBoard was my way of turning those operational questions into something concrete that I could build and verify myself.

## 3. What problem it solves

When a small service starts failing, teams often need to answer several questions quickly:

- Is the monitored endpoint actually down?
- Did the application log anything useful?
- Did an alert fire?
- Is this a service problem or an observability problem?
- Can we confirm the failure through metrics and exception tracking?

PingBoard centralizes that loop by providing:

- endpoint registration and repeated checks
- failure history and current status
- Discord alerting for repeated failures and recoveries
- Sentry-based exception validation
- Grafana dashboards for metrics and logs

## 4. Main features to explain

### Monitor management

- Register endpoints to monitor
- Group monitors by environment and tags
- Edit monitors without recreating them
- Pause or resume monitors
- Resume and immediately trigger a fresh check

### Incident flow

- Repeated failures trigger Discord alerts
- Recovery sends a separate recovery alert
- Exception verification is sent to Sentry using a dedicated test endpoint

### Observability

- Prometheus collects metrics
- Grafana visualizes monitor counts, request throughput, and check outcomes
- Promtail ships application logs to Loki
- Grafana logs panel shows actual failure events

### Deployment

- Dockerized application
- Render blueprint for hosted deployment
- GitHub Actions workflow for test and deploy-hook based automation

## 5. What I implemented directly

- Spring Boot backend with monitor registration and check execution
- PostgreSQL persistence for monitor and check history data
- Discord webhook alert flow
- Sentry integration and verification route
- Prometheus, Grafana, Loki, and Promtail local observability stack
- Render deployment configuration
- GitHub Actions automation
- Operational documents such as runbook, backup/recovery guide, and data retention notes

## 6. What was difficult

### 1. Deployment failures caused by environment-specific database configuration

One of the harder issues came after deploying to Render.
The application built successfully, but failed to start because the database connection string format from Render did not match what Spring Boot and Hikari expected directly.

I diagnosed the issue by reading the deployment logs, identified the mismatch between the Render-style Postgres URL and the JDBC URL expected by the app, and added normalization logic so the application could convert the connection string into the correct JDBC format.

This taught me that deployment readiness is not just about Docker builds succeeding; it is also about handling differences between local and hosted runtime environments.

### 2. Making observability actually usable

At first, Grafana did not show useful data even though Prometheus scraping was working.
I had to debug datasource wiring, metric names, and provisioning behavior to make the dashboard show real values.

That process helped me understand that observability is not finished just because tools are "connected"; it is only complete when the data is visible, accurate, and actionable.

## 7. What I learned

- Service operations and server operations are related, but not the same.
- For a backend developer, it is important to understand how to keep a service healthy, observable, and debuggable after deployment.
- Alerts, logs, metrics, and exception tracking become much more meaningful when they are verified together.
- Deployment success should be validated through health checks, login checks, failing test scenarios, and incident tooling, not only by seeing a service URL open.

## 8. Why this project matters for a backend role

This project demonstrates that I can think beyond endpoint implementation and look at the full lifecycle of a backend service:

- deployment
- observability
- alerting
- exception tracking
- incident response
- environment-specific troubleshooting

For a junior backend role, I think this is valuable because it shows that I am not only interested in writing API code, but also in making sure the service can be validated and operated after release.

## 9. Short answer to "What did you contribute?"

I designed and implemented a Spring Boot monitoring service that registers endpoints, runs health checks, stores status history, sends Discord alerts for repeated failures, captures verification errors in Sentry, exposes metrics through Prometheus, visualizes them in Grafana, and deploys to Render with GitHub Actions automation.

## 10. Short answer to "What would you improve next?"

If I continued the project, I would focus on:

- deleting old check history automatically
- strengthening production-only access for the Sentry test endpoint
- tuning alert noise and recovery conditions
- improving long-term retention and cleanup strategies
- adding richer deployment validation and rollback procedures
