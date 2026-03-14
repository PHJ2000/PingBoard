# Deployment Checklist

## What Is Already Prepared in This Repo

- `render.yaml` for Render Blueprint provisioning
- GitHub Actions test workflow
- GitHub Actions deploy-hook workflow
- Dockerized application image
- health check endpoint
- secret-friendly `.env.example`

## Remaining User-Only Steps

1. Create a new Render Blueprint from this repository.
2. Confirm the `pingboard-app` web service and `pingboard-db` Postgres database names.
3. Provide values for Blueprint variables marked with `sync: false`:
   - `PINGBOARD_APP_BASE_URL`
   - `PINGBOARD_ALERTS_WEBHOOK_URL`
   - `SENTRY_DSN`
   - `PINGBOARD_OPERATOR_PASSWORD`
4. After the service exists, create a Render deploy hook.
5. Add that deploy hook URL to GitHub Actions as `RENDER_DEPLOY_HOOK_URL`.
6. Optionally add a custom domain in Render.
7. Verify the custom domain, then Render will manage TLS automatically.

## First Production Validation

1. Open `/actuator/health`
2. Log in to the dashboard
3. Create one healthy monitor and one failing monitor
4. Confirm Discord alert delivery
5. Confirm Sentry test event delivery
6. Confirm Grafana metrics and logs
