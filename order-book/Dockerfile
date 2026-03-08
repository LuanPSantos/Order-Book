FROM paketobuildpacks/builder-jammy-java-tiny:latest AS Builder

WORKDIR /app/order-book

COPY --chown=gradle:gradle . .

RUN gradle nativeCompile

FROM ubuntu/glibc2.35-0ubuntu3.8

WORKDIR /app/order-book

COPY --from=Builder /app/order-book/build/native/nativeCompile/* .

CMD ["./order-book"]
