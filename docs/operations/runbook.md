# PingBoard Runbook

## Purpose

This runbook is the first place to look when PingBoard or one of the monitored endpoints starts failing.

## Health Checks

1. Open `https://<your-render-service>.onrender.com/actuator/health`.
2. Confirm the application responds with `UP`.
3. If the app is down, check Render service health and latest deploy status first.

## Alert Triage

1. Read the Discord alert payload.
2. Note:
   - monitor name
   - failing URL
   - failure count
   - last error
3. Open the PingBoard dashboard and verify the monitor's current status.

## Sentry Flow

1. Open the Sentry project.
2. Check the latest unresolved issue.
3. Inspect stack trace, tags, and request context.
4. If the issue is the synthetic `/api/dev/sentry-test` event, resolve it and continue.

## Grafana Flow

1. Open the `PingBoard Overview` dashboard.
2. Check:
   - `App Reachable`
   - `Down Monitors`
   - `HTTP Throughput`
   - `Monitor Check Outcomes`
3. Inspect `Application Logs` for the failing monitor name or exception.

## Render Flow

1. Open the Render service dashboard.
2. Confirm:
   - latest deploy succeeded
   - health checks are passing
   - service logs show no boot-time failures
3. If deploy just changed, consider rollback before making code changes.

## First Response Actions

1. Pause noisy monitors if needed.
2. Edit the monitor if the target URL changed.
3. Resume the monitor to force an immediate verification check.
4. Resolve the incident only after:
   - monitor state returns to `UP`
   - Grafana metrics stabilize
   - Sentry stops receiving new errors

## Escalation Notes

- If PingBoard is down but the monitored service is fine, treat it as an internal observability incident.
- If PingBoard is up and only one target fails, treat it as an upstream service incident.
- If multiple unrelated targets fail at the same time, check network, DNS, or hosting provider status.
