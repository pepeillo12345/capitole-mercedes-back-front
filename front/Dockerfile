# Dockerfile para el frontend Angular
# Stage 1: Build
FROM node:20-alpine AS build

WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias (incluyendo devDependencies para el build)
RUN npm ci

# Copiar el código fuente
COPY . .

# Build de la aplicación para producción
RUN npx ng build --configuration=production

# Stage 2: Serve with nginx
FROM nginx:alpine

# Copiar la configuración de nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Copiar los archivos build de Angular
COPY --from=build /app/dist/front/browser /usr/share/nginx/html

# Exponer puerto 80
EXPOSE 80

# Comando por defecto (nginx se ejecuta automáticamente)
CMD ["nginx", "-g", "daemon off;"]
