FROM maven:3.8.3-openjdk-17-slim AS build

COPY pom.xml /home/app/pom.xml

COPY link-parser/pom.xml /home/app/link-parser/
COPY link-parser/src /home/app/link-parser/src

COPY scrapper/pom.xml /home/app/scrapper/
COPY scrapper/src /home/app/scrapper/src

COPY bot/pom.xml /home/app/bot/
COPY bot/src /home/app/bot/src

RUN mvn -f /home/app/pom.xml clean package

FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /home/app/scrapper/target/*.jar /target/app.jar
COPY bot/src/main/resources/ src/main/resources/
EXPOSE 8080
ENTRYPOINT ["java","-jar","/target/app.jar"]
