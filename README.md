# NewPath — Backend

REST API for NewPath streetwear e-commerce platform, built with Spring Boot.

## Tech Stack

- **Java 25** + **Spring Boot 4**
- **Spring Security** — JWT authentication + Google OAuth2
- **PostgreSQL** + **Hibernate/JPA**
- **Stripe** — payments and webhooks
- **Cloudinary** — image storage
- **Docker** — containerization

## Features

- JWT + Refresh Token authentication
- Google OAuth2 login
- Product catalog with variants (size/color/quantity)
- Cart management (guest + authenticated)
- Stripe Checkout integration with promocodes
- Order management system
- Scheduled collection drops
- Admin panel API (dashboard stats, CRUD items, promocodes)
- Role-based access control (ROLE_USER / ROLE_ADMIN)

## API Documentation

Full API documentation available via Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

## Running with Docker

```bash
docker build -t newpath-backend .

docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host:5432/newpath \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=secret \
  -e JWT_SECRET=secret \
  -e STRIPE_SECRET_KEY=sk_... \
  -e STRIPE_WEBHOOK_SECRET=whsec_... \
  -e CLOUDINARY_CLOUD_NAME=... \
  -e CLOUDINARY_API_KEY=... \
  -e CLOUDINARY_API_SECRET=... \
  -e OAUTH_CLIENT=... \
  -e OAUTH_SECRET=... \
  newpath-backend
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| `DB_URL` | PostgreSQL connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |
| `STRIPE_SECRET_KEY` | Stripe secret key |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook secret |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret |
| `OAUTH_CLIENT` | Google OAuth client ID |
| `OAUTH_SECRET` | Google OAuth secret |
