FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app/backend/websocket-gateway

COPY backend/websocket-gateway/pom.xml .

RUN mvn dependency:go-offline -B

COPY backend/websocket-gateway/src ./src

COPY contracts /app/contracts

RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/backend/websocket-gateway/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
