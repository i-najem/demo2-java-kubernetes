FROM openjdk:11
LABEL authors="ergai"

WORKDIR /usr/src/app

COPY . /usr/src/app

RUN javac -d . src/Main.java
ENV MIN_SEC=30
ENV MAX_SEC=120

CMD ["java", "Main"]
