# Dockerfile para el backend Spring Boot
FROM openjdk:21-jdk-slim

# Instalar curl para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos de Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x ./mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar la aplicación con el plugin de Spring Boot
RUN ./mvnw clean package spring-boot:repackage -DskipTests

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/back-0.0.1-SNAPSHOT.jar"]