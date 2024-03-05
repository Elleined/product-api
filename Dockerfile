FROM openjdk:17-alpine
MAINTAINER Elleined

ENV MYSQL_HOST=marketplace_mysql_server
ENV MYSQL_USER=root
ENV MYSQL_PASSWORD=root
ENV MYSQL_PORT=3306
ENV MYSQL_DATABASE=marketplace_db

ADD ./target/*.jar marketplace-api.jar
ADD ./init.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 8083
CMD ["java", "-jar", "marketplace-api.jar"]