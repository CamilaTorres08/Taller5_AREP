## Taller5_AREP – Sistema de Gestión de Propiedades

### Resumen del Proyecto
Este proyecto es una aplicación REST de gestión de propiedades construida con Spring Boot. Ofrece operaciones CRUD (crear, leer, actualizar y eliminar) y soporta filtrado y paginación por dirección/ubicación, precio mínimo y tamaño mínimo. Un frontend mínimo se sirve como recursos estáticos para interactuar con el backend, mientras que la persistencia se realiza en una base de datos MySQL.

Características clave:
- Crear, actualizar, eliminar y consultar propiedades
- Paginación y filtros opcionales: ubicación (dirección contiene), precio (>=), tamaño (>=)
- Validación de DTO con manejo de errores claro
- Servicio contenedorizado listo para despliegue en la nube

Tecnologías principales: Spring Boot, Spring Data JPA, Hibernate, MySQL, Docker, Java 21.

### Arquitectura del Sistema
El sistema sigue una arquitectura clásica de tres capas: un frontend estático, un backend en Spring Boot y una base de datos MySQL. El frontend realiza peticiones HTTP al backend, el cual persiste datos en MySQL vía JPA.

<img src=images/despliegue.png>

- Puerto del backend: `8080` (ver `application.properties` y `Dockerfile`).
- Base de datos: MySQL 8 (driver `com.mysql.cj.jdbc.Driver`, dialecto `MySQL8Dialect`).
- Conexión a BD vía variables de entorno: `SPRING_DATASOURCE_URL`, `MYSQL_USER`, `MYSQL_PASSWORD`.

Formato de ejemplo para `SPRING_DATASOURCE_URL`:
- Local/MySQL: `jdbc:mysql://<host>:3306/<database>?useSSL=false&serverTimezone=UTC`

### Diseño de Clases
Dominio y capas principales:

<img src=images/clases1.png>

Archivos relevantes:
- `src/main/java/edu/eci/arep/taller5/model/Property.java`: Entidad JPA para propiedades.
- `src/main/java/edu/eci/arep/taller5/model/DTO/PropertyDTO.java`: DTO para validación de entrada.
- `src/main/java/edu/eci/arep/taller5/repository/PropertyRepository.java`: Repositorio Spring Data JPA con consulta de filtro.
- `src/main/java/edu/eci/arep/taller5/service/PropertyService.java` y `.../Imp/PropertyServiceImp.java`: Lógica de negocio.
- `src/main/java/edu/eci/arep/taller5/controller/PropertyController.java`: Endpoints REST.

### Resumen de la API REST
Ruta base: `/properties`

- GET `/properties`
  - Parámetros de consulta (opcionales): `location`, `price`, `sizeProperty`, y los de Spring `page`, `size`, `sort`
  - Retorna `Page<Property>`

- GET `/properties/{id}` → `Property`

- POST `/properties` → crea `Property` a partir de `PropertyDTO`

- PUT `/properties/{id}` → actualiza una `Property` existente

- DELETE `/properties/{id}` → elimina la propiedad

Ejemplos de uso:
```bash
# Listar la primera página de propiedades
curl -s "http://localhost:8080/properties?page=0&size=10"

# Filtrar por ubicación que contenga 'park', precio >= 100000, tamaño >= 50
curl -s "http://localhost:8080/properties?location=park&price=100000&sizeProperty=50"

# Crear
curl -s -X POST "http://localhost:8080/properties" \
  -H "Content-Type: application/json" \
  -d '{"address":"742 Evergreen Terrace","price":350000,"size":120,"description":"Family home"}'

# Actualizar
curl -s -X PUT "http://localhost:8080/properties/1" \
  -H "Content-Type: application/json" \
  -d '{"address":"Updated","price":360000,"size":125,"description":"Updated desc"}'

# Eliminar
curl -s -X DELETE "http://localhost:8080/properties/1" -i
```

### Desarrollo Local
Prerequisitos: Java 21, Maven 3.9+, MySQL 8 (o una instancia MySQL alojada).

1) Configura variables de entorno (ejemplo):
```bash
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/properties?useSSL=false&serverTimezone=UTC
set MYSQL_USER=root
set MYSQL_PASSWORD=secret
```

2) Construye el JAR:
```bash
./mvnw clean package -DskipTests
```

3) Ejecuta localmente:
```bash
java -jar target/taller5-0.0.1-SNAPSHOT.jar
```

El frontend estático se sirve desde `src/main/resources/static` en `http://localhost:8080/`.

### Contenerización e Imágenes
El `Dockerfile` (base Java 21) construye una imagen de ejecución para el JAR de Spring Boot.

Construcción y ejecución local:
```bash
# Build jar
./mvnw clean package -DskipTests

# Build image
docker build -t taller5-arep:latest .

# Ejecutar contenedor (conectando a MySQL local)
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/properties?useSSL=false&serverTimezone=UTC" \
  -e MYSQL_USER="root" \
  -e MYSQL_PASSWORD="secret" \
  --name taller5 taller5-arep:latest
```

Opcional: ejecutar con un MySQL acompañante usando Docker Compose (fragmento de ejemplo):
```yaml
version: "3.8"
services:
  db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: properties
      MYSQL_ROOT_PASSWORD: secret
    ports:
      - "3306:3306"
  app:
    image: taller5-arep:latest
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/properties?useSSL=false&serverTimezone=UTC
      MYSQL_USER: root
      MYSQL_PASSWORD: secret
    ports:
      - "8080:8080"
```

### Despliegue en AWS (ECR + ECS Fargate)
Configuración típica y económica usando MySQL administrado (Amazon RDS) y un backend contenedorizado en Fargate.

Prerequisitos:
- Cuenta de AWS, AWS CLI configurada
- Repositorio ECR creado (por ejemplo, `taller5-arep`)
- Instancia RDS MySQL con base de datos/esquema creado

Pasos:
1) Construir y subir la imagen a ECR
```bash
ACCOUNT_ID=<your_aws_account_id>
REGION=<your_region>
REPO=taller5-arep

aws ecr get-login-password --region $REGION | \
  docker login --username AWS --password-stdin $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com

docker build -t $REPO:latest .
docker tag $REPO:latest $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$REPO:latest
docker push $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$REPO:latest
```

2) Aprovisionar infraestructura
- Crear una instancia RDS MySQL y anotar: host, puerto, base, usuario, contraseña.
- Crear una VPC con subredes y grupos de seguridad que permitan al servicio ECS acceder a RDS por el puerto 3306.
- Crear un clúster ECS y un servicio Fargate.

3) Definir Task Definition (Fargate)
- Imagen de contenedor: `ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/taller5-arep:latest`
- Mapeo de puertos: contenedor `8080`
- Variables de entorno:
  - `SPRING_DATASOURCE_URL=jdbc:mysql://<rds-endpoint>:3306/<db>?useSSL=false&serverTimezone=UTC`
  - `MYSQL_USER=<db_user>`
  - `MYSQL_PASSWORD=<db_password>`
- Asignar el servicio a subredes con salida a internet/NAT si se necesita.

4) Crear un Load Balancer (opcional pero recomendado)
- Application Load Balancer → target group en puerto 8080 → servicio ECS como destino.
- Exponer el DNS del ALB como endpoint público.

5) Verificar el despliegue
```bash
curl http://<alb-dns-or-service-public-ip>:8080/properties
```

Alternativa: EC2
- Lanzar una instancia EC2, instalar Docker, hacer pull desde ECR y ejecutar el contenedor con las variables de RDS como arriba.

### Configuración
`src/main/resources/application.properties` usa variables de entorno:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${MYSQL_USER}
spring.datasource.password=${MYSQL_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
server.port=8080
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.open-in-view=false
```

### Pruebas
- Las pruebas de unidad y de contexto están en `src/test/java/...`.

Ejecutar todas las pruebas:
```bash
./mvnw test
```

Sugerencias para más pruebas:
- Pruebas de la capa de servicio para `PropertyServiceImp` (casos borde de crear/actualizar/eliminar)
- Pruebas de repositorio para `findAllByFilter`
- Pruebas de controlador con `@WebMvcTest` para validación y manejo de errores

### Capturas de Pantalla
Agrega capturas a `docs/` y referéncialas aquí.

- Lista CRUD: `docs/screenshot-list.png`
- Formulario de creación: `docs/screenshot-create.png`
- Formulario de actualización: `docs/screenshot-update.png`
- Confirmación/resultado de eliminación: `docs/screenshot-delete.png`

Ejemplo de inserción:
```markdown
![List](docs/screenshot-list.png)
![Create](docs/screenshot-create.png)
![Update](docs/screenshot-update.png)
![Delete](docs/screenshot-delete.png)
```

### Artefactos de Build y Ejecución
- JAR: `target/taller5-0.0.1-SNAPSHOT.jar`
- La imagen expone el puerto `8080` (ver `Dockerfile`).

### Solución de Problemas
- Conexión a BD: asegúrate de que SG/firewalls permitan 3306 desde la app hacia la BD.
- Si ejecutas Docker en Windows y conectas a MySQL local, usa `host.docker.internal` como host.
- Revisa logs: `docker logs taller5` o logs de Spring Boot para errores de validación y SQL.


