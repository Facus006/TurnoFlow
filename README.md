# TurnoFlow

API REST que permite a negocios gestionar sus turnos y a clientes reservarlos, con autenticación JWT y control de acceso por roles.

## Tecnologías
- Java
- Spring Boot
- Spring Security
- JWT
- MySQL
- Hibernate/JPA
- Docker
- Docker Compose
- Maven
- Swagger / OpenAPI
- JUnit & Mockito

## Funcionalidades
- Registro/Login
- Roles (ADMIN / CLIENTE / NEGOCIO)
- Reserva de turnos
- Confirmación/Rechazo/Cancelación
- Paginación
- Seguridad con JWT

 ## Ejecución con Docker
- docker compose up --build
- 
-La API quedará disponible en:
http://localhost:8080

-Documentación Swagger:
http://localhost:8080/swagger-ui/index.html

 #Configuración

-Crear un archivo:

src/main/resources/application.properties

-tomando como referencia:

src/main/resources/application-example.properties


## 🔗 Repositorio frontend

[TurnoFlow — Frontend](https://github.com/Facus006/TurnoFlow-Front)

## Próximamente
- Emails automáticos
- Deploy

## 📸 API Documentation

### Endpoints
![Swagger 1](https://github.com/user-attachments/assets/6abe7ee0-f022-413e-9015-8e4fae1c8f93)
![Swagger 2](https://github.com/user-attachments/assets/634a57b6-39b5-438c-abf7-9561c44396a9)

### Endpoint expandido
![Swagger detalle](https://github.com/user-attachments/assets/7ec004d5-3aab-4f8c-9a27-968ad81f51fc)
