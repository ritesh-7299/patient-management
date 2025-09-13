# Patient Management System

A microservice-based backend system for managing patient data, authentication, billing, etc. Built using Java Spring Boot, with an API Gateway and separate services for authentication, patient services, billing, etc.

---

## Table of Contents

- [Features](#features)  
- [Architecture](#architecture)  
- [Tech Stack](#tech-stack)  
- [Setup & Installation](#setup--installation)  
- [Services & Endpoints](#services--endpoints)  
- [Security](#security)  
- [Configuration](#configuration)  
- [Running Tests](#running-tests)  
- [Roadmap / Future Work](#roadmap--future-work)  
- [Contributing](#contributing)  
- [License](#license)

---

## Features

- User authentication & authorization  
- Role-based access (if applicable)  
- Patient CRUD operations (create, read, update, delete)  
- Billing service  
- API Gateway for routing and request filtering  
- (Optional) Analytics service  
- Token validation / secure endpoints

---

## Architecture

Here’s a high-level overview of how this system is structured:

```
 Client → API Gateway → [Auth Service]  
                   ↘ [Patient Service]  
                   ↘ [Billing Service]  
                   ↘ [Analytics Service] (if present)
```

- **API Gateway** handles routing, authorization checks, and acts as the single entry point.  
- **Auth Service** issues JWTs and validates incoming requests.  
- **Patient Service** manages patient data.  
- **Billing Service** handles billing and payments.  
- **Analytics Service** (if any) for reports and metrics.  

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java |
| Framework | Spring Boot / Spring Cloud Gateway / Spring Security |
| Build Tool | Maven |
| Communication | REST / gRPC |
| Database(s) | PostgreSQL |
| Auth / Security | JWT tokens, possibly OAuth2 resource server approach |
| API Documentation | swagger |
| Others | Docker (if applicable), CI/CD tools, etc. |

---

## Setup & Installation

1. **Prerequisites**  
   - Java (version X or above)  
   - Maven  
   - Database setup PostgreSQL  
   - (Optional) Docker if you use containers  

2. **Clone the repository**

   ```bash
   git clone https://github.com/ritesh-7299/patient-management.git
   cd patient-management
   ```

3. **Configure environment / properties**

   Each service may have its own `application.properties` or `application.yml`. You’ll need to set things like:

   - Database connection (URL, username, password)  
   - Auth service URL / JWK / public key (if using JWT)  
   - Ports for each microservice  

4. **Build and Run**

   ```bash
   mvn clean install
   # Then run each service, e.g.
   cd api-gateway && mvn spring-boot:run  
   cd auth-service && mvn spring-boot:run  
   cd patient-service && mvn spring-boot:run  
   # etc.
   ```

5. **Testing**

   Use Postman / curl or any REST client to hit the available endpoints.

---

## Services & Endpoints

Below are some of the services and example endpoints (adjust as necessary for your implementation):

| Service | Endpoint | HTTP Method | Description |
|---------|----------|-------------|-------------|
| Auth Service | `/auth/login` | POST | Authenticate user & get JWT |
| Auth Service | `/auth/validate` | GET | Validate token (if used) |
| Patient Service | `/patients` | GET / POST | List all patients / Create a patient |
| Patient Service | `/patients/{id}` | GET / PUT / DELETE | View / Update / Delete patient by ID |
| Billing Service | `/billing` | POST / GET | Billing operations |

---

## Security

- JWT tokens are used for authentication.  
- API Gateway filters requests to verify the token before forwarding to downstream services.  
- Only authorized roles (if implemented) can access certain endpoints.  
- (Optional) Token revocation or expiry management.

---

## Configuration

Environment-specific configs are stored in `application.yml` or `application.properties` for each service. Key configuration items:

- `server.port` for each service  
- Database credentials  
- `auth.service.url` in API Gateway  
- JWT issuer / public key or JWK set URI  
- (If applicable) any external dependencies, message brokers, etc.


---

## Roadmap / Future Work

- Move validation in API Gateway from remote call to local JWT signature verification  
- Implement role‐based access control (if not fully done)  
- Add more services (e.g. Notification, Logging, Monitoring)  
- Add retries, circuit breakers for microservices resilience  
- Deploy with Docker / Kubernetes  
- Add automated tests, coverage metrics  

---

## Contributing

Thank you for considering contributing! Here are a few guidelines:

1. Fork the repo  
2. Create a feature branch (`git checkout -b feature/YourFeature`)  
3. Write tests and update code  
4. Commit & push your changes  
5. Open a Pull Request  

---

## License

*MIT*

---

## Contact

Ritesh Macwan — riteshmacwan07@gmail.com  

Project Link: [https://github.com/ritesh-7299/patient-management](https://github.com/ritesh-7299/patient-management)
