# 🩺 Patient Management System

A **microservices-based backend system** for managing patient data, authentication, and billing. Built with **Java Spring Boot**, the system includes an **API Gateway** and separate services for authentication, patient management, and billing.

---

## Table of Contents

* [Features](#features)
* [Architecture](#architecture)
* [Tech Stack](#tech-stack)
* [Setup & Installation](#setup--installation)
* [Services & Endpoints](#services--endpoints)
* [Security](#security)
* [Configuration](#configuration)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)

---

## Features

* User authentication and JWT-based authorization
* Role-based access control
* CRUD operations for patient data
* Billing service for payments and invoices
* API Gateway for routing, request filtering, and token validation
* Optional analytics and reporting service

---

## Architecture

```
Client → API Gateway → [Auth Service]
                  ↘ [Patient Service]
                  ↘ [Billing Service]
                  ↘ [Analytics Service] (optional)
```

* **API Gateway:** Entry point for all requests, handles routing and authorization
* **Auth Service:** Issues and validates JWTs
* **Patient Service:** Manages patient records
* **Billing Service:** Handles billing operations
* **Analytics Service:** Optional service for reports and metrics

---

## Tech Stack

| Component         | Technology                                         |
| ----------------- | -------------------------------------------------- |
| Language          | Java                                               |
| Framework         | Spring Boot, Spring Cloud Gateway, Spring Security |
| Build Tool        | Maven                                              |
| Communication     | REST / gRPC                                        |
| Database          | PostgreSQL                                         |
| Auth / Security   | JWT tokens                                         |
| API Documentation | Swagger                                            |
| Deployment        | Docker, (optional) Kubernetes                      |
| Messaging         | Kafka                                              |

---

## Setup & Installation

1. **Prerequisites**

   * Java 17+
   * Maven
   * PostgreSQL database
   * Docker (optional)

2. **Clone the repo**

```bash
git clone https://github.com/ritesh-7299/patient-management.git
cd patient-management
```

3. **Configure environment variables** in each service’s `application.yml` or `application.properties` (database credentials, ports, JWT settings).

4. **Build and Run**

```bash
mvn clean install
# Example for running services:
cd api-gateway && mvn spring-boot:run
cd auth-service && mvn spring-boot:run
cd patient-service && mvn spring-boot:run
```

5. **Test endpoints** using Postman, curl, or any REST client.

---

## Services & Endpoints

| Service         | Endpoint         | Method             | Description                    |
| --------------- | ---------------- | ------------------ | ------------------------------ |
| Auth Service    | `/auth/login`    | POST               | Authenticate and obtain JWT    |
| Auth Service    | `/auth/validate` | GET                | Validate JWT token             |
| Patient Service | `/patients`      | GET / POST         | List all / create patient      |
| Patient Service | `/patients/{id}` | GET / PUT / DELETE | View / update / delete patient |
| Billing Service | `/billing`       | GET / POST         | Billing operations             |

---

## Security

* JWT-based authentication for all services
* API Gateway validates tokens before forwarding requests
* Role-based access control to restrict endpoints

---

## Configuration

Key configs are in each service’s `application.yml` or `application.properties`:

* `server.port` for services
* Database connection settings
* `auth.service.url` for API Gateway
* JWT issuer / public key

---

## Roadmap / Future Work

* Move JWT validation in API Gateway to local signature verification
* Full role-based access control
* Add services for notifications, logging, and monitoring
* Implement retries and circuit breakers for resilience
* Docker / Kubernetes deployment
* Automated testing with coverage metrics

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/YourFeature`)
3. Add tests and implement your feature
4. Commit and push changes
5. Open a Pull Request

---

## License

MIT

---

## Contact

Ritesh Macwan — [riteshmacwan07@gmail.com](mailto:riteshmacwan07@gmail.com)
Project Link: [Patient Management System](https://github.com/ritesh-7299/patient-management)
