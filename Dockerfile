FROM openjdk:21-jdk-oraclelinux9
WORKDIR /app
COPY /target/*.jar /app/code.jar
ENTRYPOINT ["java", "-jar", "code.jar"]