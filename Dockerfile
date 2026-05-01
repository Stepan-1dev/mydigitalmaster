FROM openjdk:27-ea-trixie
WORKDIR /app
COPY /target/*.jar /app/code.jar
ENTRYPOINT ["java", "-jar", "code.jar"]