FROM maven:3.9.2-eclipse-temurin-17-alpine AS build
COPY totem-food-email-backend /usr/src/app/totem-food-email-backend
COPY totem-food-email-application /usr/src/app/totem-food-email-application
COPY totem-food-email-domain /usr/src/app/totem-food-email-domain
COPY totem-food-email-framework /usr/src/app/totem-food-email-framework
COPY pom.xml /usr/src/app/pom.xml
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:17.0.2-slim-buster
LABEL maintainer="Totem Food Service"
WORKDIR /opt/app
COPY --from=build /usr/src/app/totem-food-email-backend/target/*.jar totem-food-email-service.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:8787", "-jar","/opt/app/totem-food-email-service.jar"]