# Digital Wallet & Payment Backend

A RESTful banking backend built with Spring Boot supporting 
user registration, JWT authentication, role-based access control,
P2P fund transfers, and transaction history.

## Tech Stack
Java 17 · Spring Boot 3 · Spring Security · JWT · MySQL · 
JPA/Hibernate · Docker · Swagger/OpenAPI · JUnit 5 · Mockito

## Live Demo
Swagger UI: https://digitalwallet-production.up.railway.app/docs

## Features
- User registration with auto wallet creation (₹100 bonus)
- JWT based authentication
- Role based authorization (USER / ADMIN)
- Wallet top-up
- P2P fund transfers with @Transactional (ACID compliant)
- Transaction history
- Email notifications on transfer
- Global exception handling
- Dockerized with docker-compose

## How to Run Locally
1. Clone the repo
2. Copy .env.example to .env and fill values
3. Run: mvn clean package -DskipTests
4. Run: docker-compose up --build

## API Documentation
Full API docs available at /docs after running the app
