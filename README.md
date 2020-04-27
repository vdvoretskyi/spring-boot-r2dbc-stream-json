# spring-boot-r2dbc-stream-json

BUILD the application
mvn clean package

BUILD AND UP Docker Compose
docker-compose up --build
docker-compose down <- down docker compose

CURLS

GET /hello/contact
curl -s -X GET \
  http://localhost:8080/hello/contact?nameFilter=.* \
  -H 'Accept-Type: application/json'
