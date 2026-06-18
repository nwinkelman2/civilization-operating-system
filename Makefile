.PHONY: build test run docker-up docker-down clean deploy

# Default
all: build

# Build the application
build:
	./mvnw clean package -DskipTests

# Run tests
test:
	./mvnw test

# Run in dev mode (H2)
run:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run in prod mode (PostgreSQL required)
run-prod:
	SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run

# Docker Compose
docker-up:
	docker-compose up -d --build

docker-down:
	docker-compose down

# Deploy: build production image and start
deploy: build
	docker-compose up -d --build
	@echo "App will be available at http://localhost:8080"
	@echo "Health check: http://localhost:8080/actuator/health"

# Clean
clean:
	./mvnw clean
	docker-compose down -v
