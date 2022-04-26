FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE
COPY build/libs/currency-rate-converter-2.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
