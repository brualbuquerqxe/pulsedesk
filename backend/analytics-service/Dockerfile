FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /workspace/backend/analytics-service

COPY backend/analytics-service/pom.xml .

RUN mvn dependency:go-offline -B

COPY backend/analytics-service/src ./src

COPY contracts /workspace/contracts

RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/backend/analytics-service/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
