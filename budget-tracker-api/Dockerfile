FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/budget-tracker-api-0.0.1-SNAPSHOT.jar"]