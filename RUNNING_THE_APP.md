### Project is built using:
- Embedded ActiveMQ
- Mongo running on docker
- Spring Boot App running on docker

### How to run the docker containers:
- Go to the root directory of the project and build the jar file: 

mvn clean install
- Once the jar is built, we are able to run the docker containers using docker compose: 

docker-compose up

### Once the Application is running we can reach the endpoints as follow:
- Fetch Score by the given Menu Item 

GET localhost:8080/menu/{menuItemId}/score
- Fetch Score by the given Category 

GET localhost:8080/category/{category}/score 
- Retrieve Menu Item with Score above/below a parameter

GET localhost:8080/menu/score?min=1&max=30
- Retrieve Categories with Score above/below a parameter

GET localhost:8080/category/score?min=1&max=90