<!-- @format -->

# Fruit Shop

A full-stack e-commerce web application for a fruit shop.

## Demo

**Live:** [Visit Site](https://d28chu8xpcbqbf.cloudfront.net)

## Tech Stack

| Layer    | Technology                                |
| -------- | ----------------------------------------- |
| Frontend | Angular 20, TypeScript, SCSS              |
| Backend  | Spring Boot 3.5, Java 21, Spring Data JPA |
| Auth     | Spring Security, JWT                      |
| Database | H2 (dev), MySQL (prod)                    |
| Infra    | AWS ECS Fargate, S3, CloudFront           |
| CI/CD    | GitHub Actions                            |

## Features

- Product listing and cart management
- JWT-based authentication (register/login)
- Order creation
- Contact form

## Project Structure

```
├── client/                 # Angular Frontend
│   └── src/app/
│       ├── components/     # Navbar, Footer, Cart Sidebar
│       ├── pages/          # Home, Login, Register
│       └── services/       # Auth, Cart, Product, Order
│
└── server/                 # Spring Boot Backend
    └── src/main/java/com/fruit/server/
        ├── cart/           # Cart domain
        ├── order/          # Order domain
        ├── product/        # Product domain
        └── user/           # User and authentication
```

## Getting Started

### Frontend

```bash
cd client
npm install
npm start
```

### Backend

```bash
cd server
./mvnw spring-boot:run
```

## Deployment

Automated deployment via GitHub Actions:

- Backend: ECR → ECS Fargate
- Frontend: S3 → CloudFront
