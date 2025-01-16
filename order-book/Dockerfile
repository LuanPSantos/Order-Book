FROM gradle:8-jdk21-graal AS Builder

WORKDIR /app/order-book

COPY --chown=gradle:gradle . .

RUN gradle nativeCompile

FROM ubuntu:latest

WORKDIR /app/order-book

COPY --from=Builder /app/order-book/build/native/nativeCompile/* .

CMD ["./order-book"]
