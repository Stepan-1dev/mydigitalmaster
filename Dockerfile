FROM openjdk:27-ea-oraclelinux9
WORKDIR /app
COPY /out/artifacts/JavaLearn_jar/JavaLearn.jar /app/code.jar
ENTRYPOINT ["java", "-jar", "code.jar"]