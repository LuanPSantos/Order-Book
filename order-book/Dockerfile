FROM gradle:7.4-jdk17 AS Builder

WORKDIR /app/order-book

COPY --chown=gradle:gradle . .

RUN gradle clean build --no-daemon

FROM openjdk:17.0.2-jdk-oracle

WORKDIR /app/order-book

COPY --from=Builder /app/order-book/build/libs/order-book*.jar app.jar

CMD ["java", "-jar", "./app.jar"]