# API REST - Gestión de Órdenes de Transporte

API desarrollada en **Java 11** con **Spring Boot 2.7**, **Spring Security + JWT**, **Spring JDBC (`JdbcTemplate`)**, **PostgreSQL 15** y **Docker** para la gestión de órdenes de envío, conductores activos y asignaciones con carga de archivos (`.pdf`) e imágenes (`.png`, `.jpg`).

## Tecnologías Utilizadas
* **Java 11** & **Spring Boot 2.7.18**
* **Spring Security** + **JWT (`jjwt 0.11.5`)**
* **Spring JDBC (`JdbcTemplate`)** & **PostgreSQL 15**
* **Docker** & **Docker Compose** (con `Dockerfile` multi-stage)
* **Swagger / OpenAPI 3 (`springdoc-openapi-ui`)**
* **Lombok** (`@Slf4j`, `@Data`, `@Builder`)
* **JUnit 5** & **Mockito**

---

## Requisitos Previos
* **Docker**
* **Java 11** y **Maven**

---

## Instalación y Ejecución

### 1. Clonar el repositorio

git clone https://github.com/julkman999-bigjul/ExamenTecnicoAIT
cd examen

### 2. Levantar la base de datos PostgreSQL con Docker

docker compose up -d

Esto inicia el contenedor `mi_postgres_api` en el puerto `5432` con la base de datos `examen`. Al arrancar la aplicación, Spring Boot ejecuta automáticamente `src/main/resources/schema.sql` para crear las tablas (`"order"`, `driver`, `assignment`) con tabla extra por asignacion.

### 3. Ejecutar la aplicación
**Opción A — Localmente con Maven:**

mvn spring-boot:run

*(O ejecutando la clase `ExamenTecnico.java` desde el IDE).*

## Autenticación y Credenciales

La API cuenta con seguridad configurada mediante **Spring Security** y **JWT (JSON Web Token)**:
* **Usuario:** `admin`
* **Contraseña:** `123`

### Obtener Token JWT
Realiza una petición `POST` a `/api/auth/login`:
json
{
  "username": "admin",
  "password": "123"
}

Para consumir los endpoints protegidos desde clientes externos (Postman/cURL), envía el encabezado:
text
Authorization: Bearer <TU_TOKEN_JWT>


---

## Accesos Rápidos (Navegador)
Una vez iniciada la aplicación en el puerto `8080` e iniciada la sesión con `admin` / `123`:
* **Panel Web Interactivo:** `http://localhost:8080/index.html`
* **Documentación Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

---

## Endpoints de la API

### Autenticación (`/api/auth`)
* `POST /api/auth/login` — Autentica credenciales y devuelve un token JWT válido por 2 horas.

### Órdenes (`/api/orders`)
* `POST /api/orders` — Crea una nueva orden en estado inicial `CREATED`.
* `PATCH /api/orders/{id}/status` — Actualiza el estado validando el flujo permitido (`CREATED` -> `IN_TRANSIT` -> `DELIVERED`, o `CANCELLED`).
* `GET /api/orders/{id}` — Consulta una orden específica por su `UUID`.
* `GET /api/orders` — Lista órdenes con filtros opcionales (`status`, `date`, `origin`, `destination`).

### Conductores (`/api/drivers`)
* `POST /api/drivers` — Registra un nuevo conductor activo.
* `GET /api/drivers` — Lista todos los conductores activos (`active = true`).

### Asignaciones y Archivos (`/api/assignments`)
* `POST /api/assignments` — Asigna un conductor activo a una orden en estado `CREATED` junto con sus archivos adjuntos (`pdf` e `image` en `multipart/form-data`, máximo `5MB`).
* `POST /api/assignments/{id}/pdf` — Carga o actualiza el archivo `.pdf` de una asignación.
* `POST /api/assignments/{id}/image` — Carga o actualiza la imagen (`.png`, `.jpg`) de una asignación.
* `GET /api/assignments` — Lista todas las asignaciones registradas.

---

## Pruebas Unitarias
Para ejecutar la suite de 9 pruebas unitarias con **JUnit 5** y **Mockito**:

mvn test