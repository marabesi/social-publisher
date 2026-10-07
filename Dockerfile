FROM eclipse-temurin:25-jdk-alpine
COPY ./social /app
ENTRYPOINT ["/app/bin/social"]
