# API Gateway Service 🌐

A **Spring Cloud Gateway** service that acts as a central entry point for our fintech microservices ecosystem, providing routing, filtering, and cross-cutting concerns management.

## Overview

![API Gateway Architecture](https://learn.microsoft.com/pt-pt/dotnet/architecture/microservices/architect-microservice-container-applications/media/direct-client-to-microservice-communication-versus-the-api-gateway-pattern/custom-service-api-gateway.png)

The **Gateway Service** provides:
- 🚦 Centralized routing to microservices
- 🔒 Security and authentication filtering
- 🔄 Load balancing
- 🎯 Request/response transformation
- 📊 Monitoring and logging

## Project Structure

```
src/main/java/com/fintech/gateway/
├── Config/                    # Configuration classes
│   ├── FeignConfiguration.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
├── DTO/                      # Data Transfer Objects
│   ├── TokenRequest.java
│   ├── UserResponse.java
│   └── ValidResponse.java
├── Service/                  # Service layer
│   └── FeignClient/
│       └── AuthServiceClient.java
├── GatewayApplication.java
└── resources/
    └── application.yaml      # Gateway configuration
```

## Service Routes

The Gateway routes traffic to the following services:

| Service | Path | Port |
|---------|------|------|
| User Service | `/api/users/**` | 8090 |
| Wallet Service | `/api/wallets/**` | 8099 |
| Auth Service | `/api/auth/**` | 8946 |
| Transaction Service | `/api/transaction/**` | 8599 |
| Notification Service | `/api/notifications/**` | 8085 |
| Loans Service | `/api/loans/**` | 8097 |

## Configuration

### Main Configuration
```yaml
server:
  port: 8222

spring:
  application:
    name: gateway-service
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: http://user-service:8090
          predicates:
            - Path=/api/users/**
        # Additional routes configured...
```

### Security Configuration
```yaml
var:
  filter:
    excluded-paths: /api/auth, /users/public/images, /actuator/health
```

## Features

- **Dynamic Routing**: Routes requests to appropriate microservices
- **Load Balancing**: Distributes traffic across service instances
- **Security**: JWT authentication and authorization
- **Monitoring**: Actuator endpoints for health and metrics
- **Path-Based Routing**: Configured routes based on URL paths
- **Service Discovery Integration**: Optional service discovery support
- **Health Checks**: Detailed health information for all routes

## Getting Started

1. **Prerequisites**
    - Java 17+
    - Maven
    - Running Config Server
    - Running Auth Service (for JWT validation)

2. **Configuration**
    - Ensure Config Server is running
    - Update service URIs if needed
    - Configure security settings

3. **Running Locally**
   ```bash
   mvn clean package
   java -jar target/gateway-service.jar
   ```

4. **Docker Build**
   ```bash
   docker build -t gateway-service .
   docker run -p 8222:8222 gateway-service
   ```

## Security

The gateway implements several security measures:
- JWT token validation
- Path-based security rules
- Excluded paths for public access
- CORS configuration

## Monitoring

Available actuator endpoints:
- `/actuator/health`: Service health information
- `/actuator/metrics`: Metrics data
- `/actuator/routes`: Available route configurations

## Testing Routes

Test gateway routing with:
```bash
# Test user service route
curl http://localhost:8222/api/users/profile

# Test wallet service route
curl http://localhost:8222/api/wallets/balance
```

## 👥 Team

| Avatar                                                                                                  | Name | Role | GitHub |
|---------------------------------------------------------------------------------------------------------|------|------|--------|
| <img src="https://github.com/zachary013.png" width="50" height="50" style="border-radius: 50%"/>        | Zakariae Azarkan | DevOps Engineer | [@zachary013](https://github.com/zachary013) |
| <img src="https://github.com/goalaphx.png" width="50" height="50" style="border-radius: 50%"/>          | El Mahdi Id Lahcen | Frontend Developer | [@goalaphx](https://github.com/goalaphx) |
| <img src="https://github.com/hodaifa-ech.png" width="50" height="50" style="border-radius: 50%"/>       | Hodaifa | Cloud Architect | [@hodaifa-ech](https://github.com/hodaifa-ech) |
| <img src="https://github.com/khalilh2002.png" width="50" height="50" style="border-radius: 50%"/>       | Khalil El Houssine | Backend Developer | [@khalilh2002](https://github.com/khalilh2002) |
| <img src="https://github.com/Medamine-Bahassou.png" width="50" height="50" style="border-radius: 50%"/> | Mohamed Amine BAHASSOU | ML Engineer | [@Medamine-Bahassou](https://github.com/Medamine-Bahassou) |

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-route`)
3. Commit your changes (`git commit -m 'Add new route configuration'`)
4. Push to the branch (`git push origin feature/new-route`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
