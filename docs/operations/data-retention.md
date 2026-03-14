# Data Retention Strategy

## Goal

Keep enough operational history to investigate incidents without letting observability costs grow forever.

## Recommended Defaults

### PostgreSQL

- Monitors: retain indefinitely
- Recent check history: retain 30 days in production unless compliance requires more

### Loki Logs

- Development: 7 days
- Early production: 14 days
- Mature production: 30 days if costs allow

### Prometheus Metrics

- Development: 7 to 14 days
- Early production: 14 to 30 days

### Sentry Events

- Keep within plan limits
- Retain at least enough history to compare the current incident with the previous release window

## Suggested Next Schema Improvement

Add a scheduled cleanup job for old `check_results` rows:

- keep 30 days by default
- make retention configurable per environment

## Why This Matters

Without a retention policy:

- Grafana becomes noisy
- storage costs creep up
- incident searches get slower
- old low-value data crowds out recent evidence
