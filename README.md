# Pocket Ledger 

Мини-учебный финтех-проект: REST API для управления счетами с JWT-аутентификацией, Postgres, миграциями Flyway и готовностью к Kafka. Бэкенд: Java 21 + Spring Boot 3.3.

## Стек

- Java 21, Spring Boot 3.3 (Web, Security, Data JPA, Actuator)
- PostgreSQL 16 (в Docker)
- Flyway для миграций БД
- Lombok (DTO/Entity/Builder)
- (Подготовлено место для Kafka)

## Быстрый старт

### 0) Предусловия
- JDK 21 (`/usr/lib/jvm/java-1.21.0-openjdk-amd64`)
- Docker + Docker Compose
- Maven 3.9+
- `jq` для удобного вывода JSON (опционально)

### 1) Поднять инфраструктуру (Postgres, Kafka, UI, pgAdmin)
Из корня репо:
```bash
docker compose up -d
docker ps --filter name=pocket-ledger
