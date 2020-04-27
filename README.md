# spring-boot-r2dbc-stream-json

**1. BUILD the application**
```bash
mvn clean package
```

**2. BUILD AND UP Docker Compose**
```bash
docker-compose up --build
docker-compose down <- down docker compose
```

**3. CHECK**
```bash
curl -s -X GET \
  http://localhost:8080/hello/contact?nameFilter=.* \
  -H 'Accept-Type: application/json'
```
