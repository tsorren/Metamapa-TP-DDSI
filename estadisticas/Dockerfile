# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Copiar pom.xml para cachear dependencias
COPY pom.xml .

# Descargar dependencias (se cachea esta capa si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src src

# Compilar el proyecto (sin compilar ni ejecutar tests)
RUN mvn clean package -Dmaven.test.skip=true -B -Dproject.build.sourceEncoding=UTF-8

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Instalar curl para healthcheck
RUN apk add --no-cache curl

# Copiar el JAR desde la etapa de build
COPY --from=builder /build/target/*.jar app.jar

# Exponer el puerto del servicio
EXPOSE 8084

# Healthcheck para monitoreo proactivo
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=40s \
  CMD curl -f http://localhost:8084/actuator/health || exit 1

# Variables de entorno opcionales
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

