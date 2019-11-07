# Product and order microservice

## Purpose
Restservice to manage products and orders and demonstrate some technologies
like spring boot, lombok, spock testing framework, spring data.

The rest API supports the basic CRUD operations:

* Create a new product
* Retrieve a list of all products
* Update a product
* Delete a product (soft deletion)

Orders support
* Placing an order
* Retrieving all orders within a given time period

## Build
```./gradlew build```

## Test
To run the unit test execute.

```./gradlew testRun```

## Run

```./gradlew bootRun```

## Open the application
Open
http://localhost:8080/swagger-ui.html

### Open the database console
To take a look at the in memory database you can open the following url

http://localhost:8080/h2-console

User: sa

Password: &lt;none&gt;