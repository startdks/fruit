<!-- @format -->

# Fruit Shop

과일 쇼핑몰 풀스택 웹 애플리케이션

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

-   상품 조회 및 장바구니 관리
-   JWT 기반 회원가입/로그인
-   주문 생성
-   Contact 문의

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
        ├── cart/           # 장바구니 도메인
        ├── order/          # 주문 도메인
        ├── product/        # 상품 도메인
        └── user/           # 사용자 및 인증
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

GitHub Actions를 통해 자동 배포:

-   Backend: ECR → ECS Fargate
-   Frontend: S3 → CloudFront
