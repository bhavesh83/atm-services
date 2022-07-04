FROM openjdk:8-jdk-alpine
RUN addgroup -S atm && adduser -S atm -G atm
USER atm:atm
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]