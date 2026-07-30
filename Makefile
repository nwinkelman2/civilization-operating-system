.PHONY: build build-frontend test run run-prod run-worker docker-up docker-up-all docker-down clean deploy frontend-dev

# Default
all: build

# Build the application
build:
	./mvnw clean package -DskipTests

# Build frontend
build-frontend:
	cd frontend && npm ci && npm run build

# Run frontend in dev mode
frontend-dev:
	cd frontend && npm run dev

# Run tests
test:
	./mvnw test

# Run in dev mode (H2)
run:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run in prod mode (PostgreSQL required)
run-prod:
	SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run

# Run as cortex worker (separate process)
run-worker:
	SPRING_PROFILES_ACTIVE=worker ./mvnw spring-boot:run -Dspring-boot.run.main-class=io.github.opencivilizationplatform.worker.CortexWorkerApplication

# Docker Compose (core only: postgres + app)
docker-up:
	docker-compose up -d --build postgres app app-primary

# Docker Compose (full stack: + kafka, worker, debezium)
docker-up-all:
	docker-compose up -d --build

docker-down:
	docker-compose down

# Register debezium CDC connector
setup-cdc:
	@echo "Waiting for Debezium to start..."
	@sleep 10
	@./debezium/setup-connector.sh

# Deploy: build production image and start core services
deploy: build build-frontend
	docker-compose up -d --build postgres app app-primary frontend nginx
	@echo "App will be available at http://localhost:8080"
	@echo "Frontend: http://localhost:3000"
	@echo "Health check: http://localhost:8080/actuator/health"

# Full production stack
deploy-full: build build-frontend
	docker-compose up -d --build
	@echo "Full stack deployed"
	@echo "  Frontend:   http://localhost"
	@echo "  App:        http://localhost:8080"
	@echo "  Worker:     http://localhost:9090/api/v1/worker/health"
	@echo "  Kafka:      localhost:9093"
	@echo "  Debezium:   http://localhost:8083"
	@echo "  Swagger:    http://localhost:8080/swagger-ui"

# Clean
clean:
	./mvnw clean
	docker-compose down -v
