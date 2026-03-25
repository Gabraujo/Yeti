# Build stage: compile the Spring Boot app with Maven (direct)
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -B -DskipTests clean package

# Runtime stage: slim JRE only
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/yeti-1.0.0.jar app.jar
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
