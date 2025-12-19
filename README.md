# Device API

Simple REST API to manage device entries.

## Run locally

### Development profile

Use H2 database and `dev` profile.

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
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

#### Test running app

Execute curl command to check the app is responsible to accept requests by creating a new `Device` object:

POST method:

```bash
curl -i -X POST \
  -H "Content-Type: application/json" \
  -d '{"name":"test","brand":"test"}' \
  "http://localhost:8080/api/devices"
```

Expect the same output in terminal:

```terminaloutput
HTTP/1.1 201 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 18 Dec 2025 22:54:17 GMT

{"brand":"test","creation_time":"2025-12-18T22:54:17","id":1,"name":"test","state":"AVAILABLE"}
```

## API documentation

Once the application is running, the API documentation can be accessed at:

http://localhost:8080/swagger-ui/index.html

## Health and metrics endpoints

To check health access: http://localhost:8081/actuator/health

To see the list of expose metrics visit: http://localhost:8081/actuator/metrics

After any request done to the running server requests statistics will be available here: http://localhost:8081/actuator/metrics/http.server.requests

Prometheus logs: http://localhost:8081/actuator/prometheus
