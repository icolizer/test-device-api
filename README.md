# Device API

Simple REST API to manage device entries.

## Run locally

### Development profile

Use H2 database and `dev` profile.

```bash
./gradlew :bootRun -Dspring-boot.run.profiles=dev
```

### Docker environment

Uses Postgres database and containerised application with active prod profile.

Start with build:

```bash
docker-compose up -d
```

Stop with remove:

```bash
docker-compose down
```

## API

### Create device

POST method:

```bash
curl -i -X POST \
  -H "Content-Type: application/json" \
  -d '{"name":"test","brand":"test"}' \
  "http://localhost:8080/api/devices"
```
