# 💻 Developer Guide

This guide provides instructions for setting up, building, testing, and contributing to **Civilization Operating System**.

---

## 🛠️ Prerequisites

- **Java Development Kit (JDK)**: Java 21 or higher
- **Node.js**: v18+ and `npm`
- **Docker & Docker Compose**: v2.20+
- **GitHub CLI (`gh`)**: Latest version

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone git@github.com:open-civilization-platform/civilization-operating-system.git
cd civilization-operating-system
```

### 2. Start Infrastructure with Docker Compose
Starts PostgreSQL (with PostGIS), Apache Kafka, Zookeeper, Debezium CDC, and Nginx:
```bash
docker-compose up -d
```

### 3. Run Backend (Spring Boot)
```bash
./mvnw spring-boot:run
```

### 4. Run Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```

The application will be accessible at:
- **Frontend App**: `http://localhost:5173` (or via Nginx proxy `http://localhost`)
- **Backend API**: `http://localhost:8080`
- **GraphQL Endpoint**: `http://localhost:8080/graphql`

---

## 🧪 Testing Guidelines

### Run Backend Unit & Integration Tests
```bash
./mvnw clean test
```

### Run Frontend Unit Tests
```bash
cd frontend
npm test
```

### Test Debezium CDC Pipeline
```bash
bash debezium/setup-connector.sh
curl http://localhost:8083/connectors/
```

---

## 📦 CI/CD Pipeline

Continuous Integration is managed via GitHub Actions:
- **`backend-ci.yml`**: Compiles Java 21, executes unit/integration tests with Maven.
- **`frontend-ci.yml`**: Runs ESLint, TypeScript verification, Vitest suite, and Vite build.
