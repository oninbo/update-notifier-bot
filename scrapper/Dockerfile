FROM eclipse-temurin:17-jdk-alpine
COPY target/*.jar /target/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/target/app.jar"]
