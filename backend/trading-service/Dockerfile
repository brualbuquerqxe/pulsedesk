FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /workspace/backend/trading-service

COPY backend/trading-service/pom.xml .

RUN mvn dependency:go-offline -B

COPY backend/trading-service/src ./src

COPY contracts /workspace/contracts

RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/backend/trading-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
