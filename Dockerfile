FROM openjdk:17-jdk-alpine3.14
COPY ./social /app
ENTRYPOINT ["/app/bin/social"]
