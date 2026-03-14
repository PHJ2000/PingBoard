# Backup and Recovery

## Current Strategy

PingBoard uses PostgreSQL for monitor configuration and recent check history.

## Recommended Render Setup

1. Use a managed Render Postgres instance.
2. Keep the application and database in the same region.
3. Prefer paid Render Postgres plans in production because they support stronger recovery options such as point-in-time recovery and logical exports.

## Minimum Backup Practice

1. Export the database at least once per day.
2. Store backups outside the app host.
3. Retain at least:
   - 7 daily backups
   - 4 weekly backups

## Manual Backup Command

Use `pg_dump` against the database connection string:

```bash
pg_dump "$DATABASE_URL" > pingboard-$(date +%F).sql
```

## Recovery Drill

Run this before calling the platform production-ready:

1. Restore the latest backup into a fresh database.
2. Point a temporary PingBoard instance at the restored database.
3. Confirm monitors, environments, and tags are present.
4. Confirm the dashboard and alert loop still work.

## Recovery Objectives

- Target RPO: 24 hours or better
- Target RTO: 60 minutes or better

## What to Restore First

1. Database
2. Application service
3. Secret values
4. Alerting configuration
