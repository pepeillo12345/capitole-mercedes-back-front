# 🌟 Star Wars Explorer

A full-stack application that allows you to explore the Star Wars universe using the SWAPI (Star Wars API). Built with Angular and Spring Boot, this application provides a beautiful interface to browse characters and planets from the Star Wars saga.

## ✨ Features

- **Interactive Data Tables**: Browse people and planets with pagination, sorting, and search functionality
- **Real-time Search**: Instant search with debounced queries for optimal performance
- **Responsive Design**: Modern UI built with PrimeNG and Tailwind CSS
- **Caching**: Smart caching implementation for improved performance
- **Docker Ready**: Complete containerization with Docker Compose

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular 20    │    │  Spring Boot 3  │    │   SWAPI API     │
│   Frontend      │◄──►│    Backend      │◄──►│   External      │
│   (Port 4200)   │    │   (Port 8080)   │    │   Service       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Frontend (Angular 20)
- **Framework**: Angular 20 with standalone components
- **UI Library**: PrimeNG with custom theming
- **Styling**: Tailwind CSS
- **State Management**: Signals and RxJS
- **Features**: Pagination, sorting, search

### Backend (Spring Boot 3)
- **Framework**: Spring Boot 3.5.4 with Java 21
- **API Client**: OpenFeign for external API calls
- **Caching**: Spring Cache for performance optimization
- **Architecture**: Clean architecture with facades, services, and mappers
- **Documentation**: OpenAPI/Swagger integration (TODO)

## 🚀 Quick Start with Docker

### Prerequisites

- Docker
- Git

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd star-wars-explorer
   ```

2. **Start the application**
   ```bash
   docker-compose up --build
   ```

3. **Access the application**
   - Frontend: http://localhost
   - Backend API: http://localhost:8080

### Docker Compose Services

```yaml
services:
  frontend:
    - Nginx serving Angular app
    - Port 80 (mapped to localhost:80)
    - Proxies API calls to backend
    
  backend:
    - Spring Boot application
    - Port 8080
    - Connects to SWAPI external service
```

## 🛠️ Development Setup

### Frontend Development

```bash
cd front
npm install
npm start
# Runs on http://localhost:4200
```

### Backend Development

```bash
cd back
./mvnw spring-boot:run
# Runs on http://localhost:8080
```

## 📋 Available Commands

### Docker Commands

```bash
# Build and start all services
docker-compose up --build

# Start services in background
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f

# Rebuild specific service
docker-compose build frontend
docker-compose build backend
```

### Development Commands

```bash
# Frontend
cd front
npm start          # Development server
npm run build      # Production build
npm test           # Run tests

# Backend
cd back
./mvnw spring-boot:run    # Development server
./mvnw test               # Run tests
./mvnw clean package      # Build JAR
```

### Example API Call
```bash
curl "http://localhost:8080/api/v1/people?search=luke&page=0&size=10&sort=name,asc"
```

## 🎨 UI Features

### Data Table Component
- **Pagination**: Navigate through large datasets
- **Sorting**: Click column headers to sort (name, created date)
- **Search**: Real-time search with debouncing
- **Loading States**: Skeleton loading and spinners

### Search Functionality
- Instant search with 500ms debounce
- Search across names and other relevant fields
- Clear search functionality
- No results messaging

## 🏛️ Backend Architecture

### Layers
- **Controllers**: REST endpoints with OpenAPI documentation
- **Services**: Business logic with caching
- **Facades**: External API integration and data transformation
- **Mappers**: MapStruct for DTO conversion
- **Clients**: Feign clients for SWAPI communication

### Key Features
- **Pagination Helper**: Handles SWAPI's pagination to provide unified pagination
- **Generic Sorting**: Annotation-based sorting system
- **Caching**: Redis-style caching for improved performance
- **Error Handling**: Comprehensive error handling and logging

## 🎯 Technical Highlights

### Frontend
- **Signals**: Modern Angular state management
- **Standalone Components**: Latest Angular architecture
- **Content Projection**: Flexible component composition
- **RxJS**: Reactive programming for data streams

### Backend
- **Clean Architecture**: Separation of concerns
- **Generic Components**: Reusable sorting and pagination
- **External API Integration**: Robust Feign client setup
- **Caching Strategy**: Smart caching with custom key generation

## 🧪 Testing

### Running Tests

```bash
# Frontend tests
cd front && npm test

# Backend tests
cd back && ./mvnw test
```

### Test Coverage
- Unit tests for services and components
- Integration tests for API endpoints
- Facade tests for external API integration

## 🚦 Environment Configuration

### Development
- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080`
- API calls from frontend to backend

### Production (Docker)
- Frontend: `http://localhost` (port 80)
- Backend: Internal container communication
- Nginx proxy for API calls

## 📝 Notes

- The application uses SWAPI (swapi.py4e.com) as the data source
- Sorting is available for `name` and `created` fields via API
- Other fields are sorted in memory
- Caching is implemented to reduce external API calls



