FROM openjdk:8-jdk-alpine AS builder

WORKDIR /src
COPY . . 
RUN chmod +x gradlew 
RUN ./gradlew clean build

FROM openjdk:8-jdk-alpine AS runner

VOLUME /tmp
WORKDIR /app
COPY --from=builder /src/build/libs/jira-telegram-bot.jar  app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
