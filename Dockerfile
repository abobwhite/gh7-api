FROM maven:3.5.3-jdk-8 AS build
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ADD . /usr/src/app
RUN mvn clean install

FROM openjdk:8-jre-alpine
COPY --from=build /usr/src/app/target/gh7-api.jar .
CMD ["java", "-jar", "gh7-api.jar"]
