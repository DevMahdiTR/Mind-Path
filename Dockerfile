FROM maven:3.8.6-amazoncorretto-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . .

RUN mvn clean package -DskipTests

FROM amazoncorretto:17

WORKDIR /app

COPY --from=build /app/target/*.jar /app/mindpath.jar

COPY --from=build /app/src/main/resources/serviceAccountKey.json /app/config/serviceAccountKey.json

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "mindpath.jar"]
