FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /workspace/backend/market-data-service

COPY backend/market-data-service/pom.xml .

RUN mvn dependency:go-offline -B

COPY backend/market-data-service/src ./src

COPY contracts /workspace/contracts

RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/backend/market-data-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
