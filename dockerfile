# ---- Build Stage ----
FROM gradle:9.3.1-jdk25 AS builder
WORKDIR /app
COPY build.gradle* settings.gradle* ./
COPY src ./src
RUN gradle bootJar --no-daemon

# ---- Runtime Stage ----
FROM gradle:9.3.1-jdk25
WORKDIR /app
COPY --from=builder /app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]