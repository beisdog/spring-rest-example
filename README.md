# Product and order microservice

## Purpose
Restservice to manage products and orders.

The rest API supports the basic CRUD operations:

* Create a new product
* Retrieve a list of all products
* Update a product
* Delete a product (soft deletion)

Orders support
* Placing an order
* Retrieving all orders within a given time period

## Build
./gradlew build

## Test
./gradlew testRun

## Run
./gradlew bootRun

## Open the application
Open
http://localhost:8080/swagger-ui.html