FROM maven:3-openjdk-17-slim as build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN ["mvn", "clean", "install"]

FROM openjdk:16
WORKDIR /app
COPY --from=build /app/target/api-gateway-1.0-SNAPSHOT.jar /app/api-gateway.jar
EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "/app/api-gateway.jar"]

LABEL org.opencontainers.image.source https://github.com/sonamsamdupkhangsar/api-gateway