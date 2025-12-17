# Device API

Simple REST API to manage device entries.

## Run locally

### Development profile

Use H2 database and `dev` profile.

```bash
./gradlew :bootRun -Dspring-boot.run.profiles=dev
```

### Docker environment

Start with build:

```bash
docker-compose up -d
```

Stop with remove:

```bash
docker-compose down
```
