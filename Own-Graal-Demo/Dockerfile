# graalvm
FROM ghcr.io/graalvm/jdk-community:21

# Argument for specifying the JAR file
ARG JAR_FILE=./target/*.jar

# Copy the JAR into the image
COPY ${JAR_FILE} app.jar

# Set the entry point to run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]


# Please build the image with below command to specific the image name
# docker build -t own-graal-demo:1.0-SNAPSHOT .
