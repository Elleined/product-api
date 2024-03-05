FROM openjdk:17-alpine
MAINTAINER Elleined

RUN mkdir "images" \
    cd images/
RUN mkdir "PrivateChatPictures"
RUN mkdir "PROFILE_PICTURES_FOLDER"
RUN mkdir "VALID_IDS_FOLDER"
RUN mkdir "SHOP_PICTURES_FOLDER"
RUN mkdir "PRODUCT_PICTURES_FOLDER"
RUN mkdir "DEPOSIT_TRANSACTIONS_FOLDER"
RUN mkdir "WITHDRAW_TRANSACTIONS_FOLDER"

# MySQL Server Credential
ENV MYSQL_HOST=marketplace_mysql_server
ENV MYSQL_USER=root
ENV MYSQL_PASSWORD=root
ENV MYSQL_PORT=3306
ENV MYSQL_DATABASE=marketplace_db
# App
ENV IMG_UPLOAD_DIRECTORY=./images

COPY ./target/*.jar marketplace-api.jar
COPY src/main/resources/sql/init.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 8083
CMD ["java", "-jar", "marketplace-api.jar"]