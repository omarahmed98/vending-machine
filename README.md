# Vending Machine API Documentation

## Introduction
This API provides endpoints for managing a vending machine. It allows users with different roles to perform various actions, such as adding, updating his own products, or removing his own products, depositing coins, and making purchases.

## Technologies Used

- Java Spring Boot (version 3.2.2)
- MySQL Database

## Documentation by Swagger
- Base URL for accessing the API with Swagger: [Swagger UI](http://localhost:8080/swagger-ui/index.html#/)

## Base URL
- Base URL for accessing the API: [API Base URL](http://localhost:8080)

## Test Users
- to test the endpoints you could singin with these test users which are stored in deployed database.
  - **seller**: username (test_sell) and password (seller1234).
  - **buyer**: username (test_buy) and password (buyer1234).

## Authentication
- To register an account (buyer or seller) role must be chosen.
- The API supports two roles:
  - **seller**: Users with the seller role can add, update, or remove products.
  - **buyer**: Users with the buyer role can deposit coins, make purchases, and reset their deposit.
- Authentication is required for all endpoints except POST methods in the user controller (Register and Login).

**Note**: When a token is not sent with the request, the following error appears: 
- can't parse JSON. Raw result: {"error": "Error in Accessing the resource: Cannot invoke "String.substring(int)" because "authHeader" is null"}

## Request/Response Format
All requests and responses are in JSON format.

## Models
### Product
- **productId**: Unique identifier for the product.
- **amountAvailable**: Number of units of the product available in the vending machine.
- **cost**: Cost of the product in cents.
- **productName**: Name of the product.
- **sellerId**: Identifier of the seller who added the product.

### User
- **userId**: Unique identifier for the user.
- **username**: Username of the user.
- **password**: Password of the user.
- **deposit**: Amount of money deposited by the user in cents.
- **role**: Role of the user (seller or buyer).

## Endpoints
### Users
- **POST /api/v1/users/register**: Create a new user. (No authentication required)
- **POST /api/v1/users/signin**: Login by username and password. (No authentication required)
- **GET /api/v1/users**: Get details of the logged-in user. (Authentication required)
- **PUT /api/v1/users**: Update username and/or role of the logged-in user. (Authentication required)
- **DELETE /api/v1/users**: Delete the logged-in user. (Authentication required)

### Products
- **GET /api/v1/product**: Get a list of all products. (Authentication required)
- **GET /api/v1/product/{productId}**: Get details of a specific product. (Authentication required)
- **POST /api/v1/product**: Add a new product. (Authentication required)
- **PUT /api/v1/product/{productId}**: Update details of a product. (Authentication required)
- **DELETE /api/v1/product/{productId}**: Delete a product. (Authentication required)

### Checkout
- **POST /api/v1/checkout/deposit**: Deposit coins into the vending machine account. (Authentication required)
- **POST /api/v1/checkout/buy**: Purchase products with deposited money. (Authentication required)
- **POST /api/v1/checkout/reset**: Reset the deposit amount to zero. (Authentication required)

## Notes
- Database: is deployed into server, so you haven't to update the parameters of the database in application.properties.
