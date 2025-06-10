# 🛒 Microservice-Based Scalable Backend for E-commerce Applications
Built with Spring Boot & Spring Cloud, this backend system provides a distributed, scalable, and secure infrastructure tailored for modern e-commerce platforms.

## ⚙️ Architecture Overview
This project follows a fully microservice-oriented architecture using Spring Cloud components and is designed to scale independently for each module. All services are discoverable and communicate securely via the API Gateway with asymmetric JWT authentication.

                     ┌────────────────────┐
                     │    Client / UI     │
                     └────────┬───────────┘
                              │ 
                        [ API Gateway ]
                              │
               ┌──────────────┴────────────────┬
               │                               │                     
          [User Service]               [Product Service] ─── [Payment-Service]   
               │                               │      
               └───────────────┬───────────────┴
                               │  
                    [Eureka Discovery Server]
                               │                       
                    [Config Server + Redis]
                               │
                            [Kafka]
                               │
                          [Log Service]


## 📦 Services Breakdown

| Service                | Responsibilities                                                            |
| ---------------------- | --------------------------------------------------------------------------- |
| 🔐 **User-Service**    | Manages user registration, login, token generation, profile, roles.         |
| 🛍 **Product-Service** | Manages products, categories, inventory, and order logic.                   |
| 📄 **Log-Service**     | Captures logs across all services via Kafka (decoupled async logging).      |
| 🌐 **API-Gateway**     | Central access point. Routes & validates JWTs using **asymmetric signing**. |
| 📚 **Config-Server**   | Centralized configuration using Git-backed config repository.               |
| 🧭 **Eureka-Server**   | Service discovery for load balancing & resilience.                          |


## 🚀 Tech Stack

| Tech                  | Description                              |
| --------------------- | ---------------------------------------- |
| **Java 17**           | Base language                            |
| **Spring Boot 3.x**   | Microservice framework                   |
| **Spring Cloud**      | Eureka, Config, Gateway                  |
| **Spring AOP**        | Cross-cutting concerns like logging, security checks, and exception handling                  |
| **Spring Security**   | Role-based secured endpoints             |
| **JWT (RSA)**         | Asymmetric token signing/validation      |
| **Redis**             | Caching for user tokens & orders         |
| **Kafka**             | Asynchronous log event streaming         |
| **ElasticSearch**     | Full-text search & analytics (planned)   |
| **JUnit + Mockito**   | Unit & integration testing               |
| **Docker (optional)** | Containerization & service orchestration |
| **MySQL**             | Relational database for persistent storage |


### ✨ Features

- ✅ Asymmetric JWT Security (RS256 - private/public key pair)

- 🛡️ Role-based access control via Spring Security

- 🚀 Scalable architecture via independent microservices

- 🧠 Clean logging via Kafka → Log Service → ElasticSearch (planned)

- 🧰 Centralized configuration via Config Server

- 📡 Smart routing & filtering via API Gateway

- 🧪 Extensive test coverage (unit, integration, controller)

- 🔄 Caching strategies with Redis

- 📦 Designed for CI/CD & cloud deployment


### 🧪 Testing Strategy

| Layer               | Tools Used                     | Status      |
| ------------------- | ------------------------------ | ----------- |
| ✅ Unit Tests        | JUnit, Mockito                 | Implemented |
| ✅ Integration Tests | SpringBootTest, @Transactional | Implemented |
| ✅ Controller Tests  | MockMvc                        | Implemented |


### 🚧 TODO & Future Work
- ElasticSearch integration in Log Service

- Docker Compose for local orchestration

- API Rate Limiting via Gateway Filters

- Prometheus + Grafana monitoring setup

## 🚀 Installation

### Prerequisites
- Java 17 or higher
- MySQL database
- Maven

### Setup Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/canaxs/MicroPulse.git

   cd MicroPulse
   ```

2. **Create Databases:**

   You need to create the following MySQL databases manually:

    - pulse

   You can create them using a MySQL client or CLI:
   ```
   CREATE DATABASE pulse;
   ```

3. **Update Configuration:**

   Update your bootstrap.yml or application.yml files for each service (user-service, product-service) with your own MySQL username and password.

   Example bootstrap.yml (user-service , product-service) :

      ```
      spring:
      datasource:
         url: jdbc:mysql://localhost:3306/pulse
         username: [your-database-username]
         password: [your-database-password]
      ```

4. **Build the project:**
   ```bash
   mvn clean install
   ```

5. **Launch the application:**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application:**
   The application will be available at http://localhost:8082

## 📡 API Usage

### 👤 User Management ( /user-service )

| Endpoint       | Method | Description                                |
| -------------- | ------ | ------------------------------------------ |
| `/register`    | POST   | Register a new user                        |
| `/login`       | POST   | Authenticate and retrieve JWT token        |
| `/all`         | GET    | Retrieve all users (admin access)          |
| `/by-id/{id}`  | GET    | Get a user by ID                           |
| `/update/{id}` | PUT    | Update user information                    |
| `/delete/{id}` | DELETE | Delete a user by ID                        |
| `/me`          | GET    | Get currently authenticated user's details |


### 🧾 Product Management ( /product-service )

| Endpoint                    | Method | Description                                     |
| --------------------------- | ------ | ----------------------------------------------- |
| `/create`                   | POST   | Create a new product                            |
| `/all`                      | GET    | Retrieve all products (paginated)               |
| `/by-id/{id}`               | GET    | Get a product by ID                             |
| `/by-name/{name}`           | GET    | Get products matching a name                    |
| `/by-category/{categoryId}` | GET    | Get products by category                        |
| `/delete/{id}`              | DELETE | Delete a product by ID                          |
| `/update/{id}`              | PUT    | Update product details                          |
| `/search`                   | GET    | Full-text product search (ElasticSearch-backed) |


### 🗂️ Category Management ( /product-service )

| Endpoint       | Method | Description                   |
| -------------- | ------ | ----------------------------- |
| `/create`      | POST   | Create a new product category |
| `/all`         | GET    | Retrieve all categories       |
| `/update/{id}` | PUT    | Update category by ID         |
| `/delete/{id}` | DELETE | Delete a category by ID       |
| `/category-id/{id}`| GET | Get a category by ID                                           |
| `/category-tree/{parentId}`| GET | Retrieve all child categories under a given parent |
| `/root-categories` | GET | Retrieve all root-level (top parent) categories |
| `/category-depth/{level}` | GET | Retrieve categories at a specific depth level in the hierarchy |



### 🛒 Order Management ( /product-service )

| Endpoint                                            | Method | Description                                         |
| --------------------------------------------------- | ------ | --------------------------------------------------- |
| `/create`  | POST   | Create a new order with a list of items             |
| `/order-by/{orderId}`| GET    | Retrieve order details by order ID                  |
| `/all` | GET    | Retrieve paginated list of all orders               |
| `/status/{orderId}?status=...`| PUT    | Update order status (e.g., SHIPPED, CANCELLED)      |
| `/delete/{orderId}` | DELETE | Delete order by ID                                  |
| `/add-items/{orderId}` | POST   | Add additional items to an existing order           |
| `/total-amount/{orderId}`| GET    | Calculate total amount of a specific order          |
| `/by-user`| GET    | Get all orders for the currently authenticated user |
| `/items/{orderId}`  | GET    | Get all items of a specific order                   |
| `/update-item/{orderItemId}?quantity=...&price=...`| PUT    | Update quantity and price of an order item |
| `/remove-item/{orderItemId}`| DELETE | Remove a specific order item                        |
| `/by-date-range?startDate=...&endDate=...`| GET    | Retrieve orders placed within a date range |




## 📄 License

- This project is licensed under the MIT License.

- Feel free to fork, contribute or use it as a boilerplate for your own e-commerce backend project!
