# Use a base image with Java 21
FROM openjdk:21
# Copy the JAR package into the image
COPY build/libs/bazargpt-0.0.1-SNAPSHOT.jar /app.jar
# Expose the application port
EXPOSE 8080
# Run the App
ENTRYPOINT ["java", "-jar", "/app.jar"]