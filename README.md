# PingBoard

PingBoard는 서비스 엔드포인트를 주기적으로 점검하고, 상태 변화와 이력을 기록하며, 장애 발생 시 알림과 관측 흐름까지 함께 확인할 수 있도록 만든 Spring Boot 기반 상태 점검 서비스입니다.

## 주요 기능

- 모니터 등록 / 수정 / 일시정지 / 재개
- 수동 체크 실행
- 최근 체크 이력 조회
- 상태 요약 정보 조회
- 30초 주기 스케줄 기반 상태 점검
- Discord 웹훅 알림
- HTTP Basic 기반 운영자 인증
- `/actuator/health`, `/actuator/prometheus` 노출

## 관측 및 배포 구성

- Prometheus
- Grafana
- Loki / Promtail
- Sentry
- Render 배포 설정
- GitHub Actions CI / deploy workflow

## 프로젝트 구조

```text
src/main/java/com/pingboard/
  monitor/
    api/
    domain/
    repository/
    service/
  alert/
  security/
  dev/
  config/
```

## 주요 파일

- `src/main/java/com/pingboard/monitor/service/MonitorService.java`
- `src/main/java/com/pingboard/monitor/service/MonitorCheckScheduler.java`
- `src/main/java/com/pingboard/security/config/SecurityConfig.java`
- `src/main/java/com/pingboard/monitor/api/MonitorController.java`
- `.github/workflows/ci.yml`
- `.github/workflows/render-deploy.yml`
- `docker-compose.yml`
- `prometheus/prometheus.yml`

## 기술 스택

- Java 21
- Spring Boot
- Spring Web / Validation / Data JPA / Security / Actuator
- H2 / PostgreSQL
- Prometheus / Grafana / Loki / Promtail
- Sentry
- Docker / Render / GitHub Actions

## 실행 방법

### H2로 빠르게 실행

```bash
./gradlew bootRun
```

### PostgreSQL 프로필로 실행

```bash
docker compose up -d postgres
set SPRING_PROFILES_ACTIVE=postgres
./gradlew bootRun
```

### 전체 관측 스택 실행

```bash
docker compose up --build
```
