FROM eclipse-temurin:21-jdk-alpine
COPY ./social /app
ENTRYPOINT ["/app/bin/social"]
