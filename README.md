# Vending Machine API Documentation
Introduction
This API provides endpoints for managing a vending machine. It allows users with different roles to perform various actions, such as adding, updating, or removing products, depositing coins, and making purchases.

Documentation by Swagger
The base URL for accessing the API with swagger http://localhost:8080/swagger-ui/index.html#/
The base URL for accessing the API http://localhost:8080/swagger-ui/index.html#/

Authentication
Account must be registered with role to be signed and use the Bearer token for calling endpoints.
The API supports two roles:
seller: Users with the seller role can add, update, or remove products.
buyer: Users with the buyer role can deposit coins, make purchases, and reset their deposit.

Authentication is required for all endpoints based except POST methods in user controller (Register and Login)

When Token doesn't send with request this error appears:
can't parse JSON.  Raw result:
{"error": "Error in Accessing the resource: Cannot invoke "String.substring(int)" because "authHeader" is null"}

Request/Response Format
All requests and responses are in JSON format.

Models
Product
productId: Unique identifier for the product.
amountAvailable: Number of units of the product available in the vending machine.
cost: Cost of the product in cents.
productName: Name of the product.
sellerId: Identifier of the seller who added the product.
User
userId: Unique identifier for the user.
username: Username of the user.
password: Password of the user.
deposit: Amount of money deposited by the user in cents.
role: Role of the user (seller or buyer).
Endpoints
Users
POST api/v1/users/register: Create a new user. (No authentication required)
POST api/v1/users/signin: login by username and password. (No authentication required)
GET api/v1/users: Get details of logged user. (Authentication required)
PUT api/v1/users: Update username and/or role of logged user. (Authentication required)
DELETE api/v1/users: Delete the logged user. (Authentication required)
Products
GET api/v1/product: Get a list of all products. (Authentication required)
GET api/v1/product/{productId}: Get details of a specific product. (Authentication required)
POST api/v1/product: Add a new product. (Authentication required)
PUT api/v1/product/ProductEntity: Update details of a product. (Authentication required)
DELETE api/v1/product/{productId}: Delete a product. (Authentication required)
Checkout
POST api/v1/checkout/deposit: Deposit coins into the vending machine account. (Authentication required)
POST api/v1/checkout/buy: Purchase products with deposited money. (Authentication required)
POST api/v1/checkout/reset: Reset the deposit amount to zero. (Authentication required)

Notes
Database: add the datasource.url, username, password in vending-machine\src\main\resources\application.properties.

