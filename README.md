# marketplace-api
REST API for marketplace microservice

# Features
  - Moderator
  - E-commerce API(Buy and Sell)
  - Incentives
  - Referral
  - Sustainability
  - Premium user (Buyer and Seller)

# Technologies used
  - Spring boot
  - Spring mvc
  - Spring data jpa
  - Hibernate validator
  - Spring security
  - Mapstruct
  - Lombok
  - Sofware used
      - MySQL
      - Postman
      - IntelliJ
   
# Run with Docker
1. Create Network
```
docker network create marketplace_network
```

2. Docker Run MySQL Server Image
```
docker run -itd --rm -p 3307:3306 --network marketplace_network --name marketplace_mysql_server -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=marketplace_db mysql:8.0.32
```

3. Docker Run Social Media API
```
docker run -itd --rm -p 8081:8081 --network marketplace_network --name marketplace_sma --env-file ./src/java/resources/env/sma_env.txt  sma:latest
```

4. Docker Run Philippine Location API
```
docker run -itd --rm -p 8082:8082 --network marketplace_network --name marketplace_pla --env-file ./src/java/resources/env/pla_env.txt pla:latest
```

5. Docker Run Email Sender API
```
docker run -itd --rm -p 8091:8091 --network marketplace_network --name marketplace_esa --env-file ./src/java/resources/env/esa_env.txt esa:latest
```

6. Docker Run Marketplace API
```
docker run -itd --rm -p 8083:8083 --network marketplace_network --name marketplace_app marketplace:latest
```

# Check api endpoints in Postman
[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/26932885-bd813a56-8efd-4ce1-a019-1a2259b12090?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D26932885-bd813a56-8efd-4ce1-a019-1a2259b12090%26entityType%3Dcollection%26workspaceId%3D8b17275e-bbec-484d-b763-3e88a3f91e7f)
