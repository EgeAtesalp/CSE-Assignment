FROM openjdk:8-slim as builder

WORKDIR /app

COPY ["build.gradle", "gradlew", "./"]
COPY gradle gradle

COPY . .
RUN chmod +x gradlew
RUN ./gradlew installDist

EXPOSE 9090
ENTRYPOINT ["/app/build/install/hipstershop/bin/commentservice"]