# Build Stage: Node.js wird verwendet, um die App zu bauen
FROM node:24-alpine AS build-frontend

# Set working directory
WORKDIR /App

# copy package.json and package-lock.json
COPY ./tresorfrontend/package*.json /App/

# install Node.js-dependencies
RUN npm install

# copy application files
COPY ./tresorfrontend /App/

# build the app
RUN npm run build

# Start with an image containing JDK 17
FROM eclipse-temurin:17-jdk-jammy AS build

# Install Maven
RUN apt-get update && apt-get install -y --no-install-recommends maven git \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Setup working directory
WORKDIR /App

# Copy the pom.xml file to the image
COPY ./tresorbackend/pom.xml /App/

# Cache maven dependencies
RUN mvn dependency:resolve

# Copy the entire src directory to the image
COPY ./tresorbackend/src /App/src
# Copy built frontend
COPY --from=build-frontend /App/build /App/src/main/resources/static/

# Build the app
RUN mvn clean package -Dmaven.test.skip=true

# Current dir is /App
# ==> You will find the result in folder target/

# Second part
#============

# start with a small image
FROM alpine:latest

# add JRE 17
RUN apk add --no-cache openjdk17-jre

# Set working directory
WORKDIR /App

# copy jar form first image to second image
COPY --from=build /App/target/*.jar /App/

# db has to start for backend
COPY ./tresorbackend/wait-for-db.sh /usr/local/bin/wait-for-db.sh
RUN chmod +x /usr/local/bin/wait-for-db.sh

# Expose Port
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar *.jar"]