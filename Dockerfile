FROM eclipse-temurin:17

LABEL maintainer="test@gmail.com"

WORKDIR /app

COPY build/libs/*SNAPSHOT.jar /app/employee.jar

ENTRYPOINT ["java", "-jar", "employee.jar"]


