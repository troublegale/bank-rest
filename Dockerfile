FROM maven:3.9.11-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY .env .env
ENTRYPOINT ["java", "-jar", "app.jar"]