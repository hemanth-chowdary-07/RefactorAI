# 🚀 RefactorAI

[![Live Demo](https://img.shields.io/badge/Live-Demo-brightgreen)](https://refactor-ai-eight.vercel.app/)
[![GitHub](https://img.shields.io/badge/GitHub-Repository-blue)](https://github.com/hemanth-chowdary-07/RefactorAI)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> AI-powered Java code analysis and refactoring tool built with Spring Boot, React, and Groq AI

---

## 📋 Table of Contents

- [About](#about)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)
- [Screenshots](#screenshots)
- [Challenges & Learnings](#challenges--learnings)
- [Future Improvements](#future-improvements)
- [Contributing](#contributing)
- [Contact](#contact)

---

## 🎯 About

RefactorAI is a full-stack web application that analyzes Java code quality and provides AI-powered refactoring suggestions. Built to experience the complete software development lifecycle - from architecture design to production deployment.

**🔗 Live Demo:** [https://refactor-ai-eight.vercel.app/](https://refactor-ai-eight.vercel.app/)

### Why I Built This

This project was created to gain hands-on experience with:
- ✅ Full-stack development (Spring Boot + React)
- ✅ Authentication & Security (JWT)
- ✅ Third-party API integration (Groq AI)
- ✅ Containerization (Docker)
- ✅ Cloud deployment (Render + Vercel)
- ✅ Production debugging and troubleshooting

---

## ✨ Features

### 🔐 User Authentication
- Secure signup and login with JWT
- Password hashing with BCrypt
- Token-based session management
- Protected API endpoints

### 🔍 Code Analysis
- **Deep Nesting Detection** - Identifies excessive if-statement nesting
- **Magic Number Detection** - Finds hardcoded numeric literals
- **String Concatenation** - Detects inefficient string operations in loops
- **Long Method Detection** - Flags methods exceeding optimal length
- AST-based parsing using JavaParser

### 🤖 AI-Powered Refactoring
- Integration with Groq's LLaMA 3 8B model
- Intelligent code refactoring suggestions
- Context-aware improvements
- Error handling and fallback mechanisms

### 📊 Analysis History
- Save and retrieve past analyses
- Timestamp tracking
- User-specific history management
- Issue type categorization

---

## 🛠️ Tech Stack

### Backend
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)
![H2](https://img.shields.io/badge/H2-Database-blue?style=for-the-badge)

- **Framework:** Spring Boot 3.4.12
- **Language:** Java 17
- **Authentication:** JWT (JSON Web Tokens)
- **Database:** H2 (In-Memory)
- **ORM:** JPA/Hibernate
- **Security:** BCrypt password hashing
- **Code Analysis:** JavaParser library
- **Build Tool:** Maven

### Frontend
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

- **Library:** React 18
- **Build Tool:** Vite
- **HTTP Client:** Axios
- **Styling:** CSS3

### DevOps & Infrastructure
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Render](https://img.shields.io/badge/Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)
![Vercel](https://img.shields.io/badge/Vercel-000000?style=for-the-badge&logo=vercel&logoColor=white)

- **Containerization:** Docker
- **Backend Hosting:** Render
- **Frontend Hosting:** Vercel
- **CI/CD:** GitHub Actions (auto-deploy)

### AI Integration
- **API:** Groq AI
- **Model:** LLaMA 3 8B
- **Use Case:** Code refactoring suggestions

---

## 🏗️ Architecture

```
┌─────────┐
│  User   │
└────┬────┘
     │ HTTPS
     ▼
┌─────────────────┐
│   Frontend      │
│  React + Vite   │
│ (Vercel CDN)    │
└────┬────────────┘
     │ REST API (JWT)
     ▼
┌─────────────────┐          ┌──────────────┐
│    Backend      │◄────────►│  Groq AI API │
│  Spring Boot    │  HTTPS   │  LLaMA 3 8B  │
│   (Render)      │          └──────────────┘
└────┬────────────┘
     │ JPA/Hibernate
     ▼
┌─────────────────┐
│  H2 Database    │
│  (In-Memory)    │
└─────────────────┘
```

### Data Flow

**Authentication:**
1. User submits credentials → Frontend
2. Frontend sends to `/api/auth/login` → Backend
3. Backend validates & generates JWT → Returns token
4. Frontend stores token → Includes in all subsequent requests

**Code Analysis:**
1. User pastes code → Frontend
2. Frontend sends to `/api/code/analyze` with JWT → Backend
3. Backend validates token → Parses code (AST)
4. Detects issues → Sends to Groq AI
5. AI returns refactored code → Backend saves to DB
6. Results returned → Frontend displays

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Node.js 16+ and npm
- Maven 3.6+
- Docker (optional, for containerized deployment)
- Groq API key ([Get it here](https://console.groq.com/))

### Installation

#### 1. Clone the repository

```bash
git clone https://github.com/hemanth-chowdary-07/RefactorAI.git
cd RefactorAI
```

#### 2. Backend Setup

```bash
cd refactor-ai-backend

# Create application.properties (if not exists)
touch src/main/resources/application.properties
```

Add the following to `application.properties`:

```properties
# Server Configuration
server.port=8080

# JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidation12345
jwt.expiration=86400000

# Groq API Configuration
groq.api.key=YOUR_GROQ_API_KEY_HERE

# Database Configuration (H2)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

# CORS Configuration (update with your frontend URL)
cors.allowed-origins=http://localhost:5173
```

**Build and run:**

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Backend will start at `http://localhost:8080`

#### 3. Frontend Setup

```bash
cd ../refactor-ai-frontend

# Install dependencies
npm install

# Update API URL in src/services/api.js
# Change API_BASE_URL to 'http://localhost:8080/api'

# Run development server
npm run dev
```

Frontend will start at `http://localhost:5173`

---

## 💻 Usage

### 1. Sign Up / Login

- Navigate to the application
- Create a new account or login with existing credentials
- JWT token will be stored automatically

### 2. Analyze Code

- Paste your Java code in the editor
- Click **"Analyze & Refactor"**
- Wait for analysis (typically 5-10 seconds)

### 3. View Results

**Detected Issues:**
- View code smells with line numbers
- Read detailed descriptions
- Understand severity

**AI Refactored Code:**
- See AI-generated improvements
- Compare with original code
- Copy refactored version

### 4. Check History

- Click **"History"** tab
- View all past analyses
- See timestamps and issue counts

---

## 📡 API Documentation

### Authentication Endpoints

#### Signup
```http
POST /api/auth/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "message": "User registered successfully"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "user@example.com"
}
```

### Code Analysis Endpoints

#### Analyze Code
```http
POST /api/code/analyze
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "code": "public class Example { ... }"
}
```

**Response:**
```json
{
  "issues": [
    {
      "type": "Magic Number",
      "line": 5,
      "description": "Magic number '100' found..."
    }
  ],
  "refactoredCode": "public class Example { private static final int...",
  "timestamp": "2026-01-31T10:30:00"
}
```

#### Get History
```http
GET /api/code/history
Authorization: Bearer {JWT_TOKEN}
```

**Response:**
```json
[
  {
    "id": 1,
    "code": "...",
    "issues": [...],
    "timestamp": "2026-01-31T10:30:00"
  }
]
```

---

## 🐳 Deployment

### Docker Deployment

#### Backend (Dockerfile)

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE ${PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build and run:**
```bash
docker build -t refactorai-backend .
docker run -p 8080:8080 \
  -e JWT_SECRET=yourSecret \
  -e GROQ_API_KEY=yourKey \
  refactorai-backend
```

### Render Deployment

1. Connect GitHub repository
2. Set environment to **Docker**
3. Add environment variables:
   - `GROQ_API_KEY`
   - `GROQ.API.KEY`
   - `JWT_SECRET`
   - `JWT_EXPIRATION`
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_DRIVER_CLASS_NAME`
   - `SPRING_JPA_HIBERNATE_DDL_AUTO`
4. Deploy!

### Vercel Deployment

1. Connect GitHub repository
2. Set root directory: `refactor-ai-frontend`
3. Framework: Vite
4. Build command: `npm run build`
5. Output directory: `dist`
6. Deploy!

**Important:** Update `API_BASE_URL` in `src/services/api.js` to your Render backend URL.

---

## 📸 Screenshots

### Dashboard - Code Analysis
![Dashboard](./screenshots/dashboard.png)
*Main interface with code editor and analysis results*

### Detected Issues
![Issues](./screenshots/issues.png)
*List of code smells with line numbers and descriptions*

### AI Refactored Code
![Refactored](./screenshots/refactored.png)
*AI-generated improved code with best practices*

### Analysis History
![History](./screenshots/history.png)
*Past analyses with timestamps and issue counts*

---

## 🎓 Challenges & Learnings

### Challenge 1: CORS Configuration

**Problem:** Frontend couldn't communicate with backend across different domains.

**Solution:** Configured Spring Security to allow specific origins with proper headers.

**Learning:** Understanding cross-origin security, preflight requests, and browser security policies.

---

### Challenge 2: Environment Variables

**Problem:** App crashed on deployment due to missing configurations.

**Solution:** Properly configured environment variables on Render platform.

**Learning:** Secrets management, environment-specific configurations, production vs development settings.

---

### Challenge 3: Docker Port Binding

**Problem:** Backend wasn't exposing ports correctly in Docker container.

**Solution:** Used `${PORT}` environment variable instead of hardcoded 8080.

**Learning:** Container networking, port mapping, cloud platform requirements.

---

### Challenge 4: JWT Authentication

**Problem:** Token validation failing intermittently.

**Solution:** Implemented proper token expiration handling and error responses.

**Learning:** Stateless authentication, token lifecycle, security best practices.

---

### Challenge 5: AI API Integration

**Problem:** Groq API returning 401 errors.

**Solution:** Fixed API key format and implemented proper error handling.

**Learning:** Third-party API integration, error handling, graceful degradation.

---

## 🔮 Future Improvements

### Features
- [ ] Support for Python, JavaScript, and other languages
- [ ] Batch processing for entire codebases
- [ ] GitHub integration for PR analysis
- [ ] Custom rule definitions
- [ ] Team collaboration features
- [ ] Performance metrics (cyclomatic complexity, maintainability index)

### Technical
- [ ] Switch to PostgreSQL for persistent data
- [ ] Add Redis caching for frequently analyzed patterns
- [ ] Implement comprehensive testing (unit, integration, E2E)
- [ ] Add CI/CD pipeline with GitHub Actions
- [ ] Rate limiting and API quota management
- [ ] WebSocket for real-time analysis updates

### UI/UX
- [ ] Syntax highlighting in code editor
- [ ] Dark mode support
- [ ] Diff view for before/after comparison
- [ ] Export analysis reports (PDF, JSON)
- [ ] Mobile-responsive design improvements

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java and React best practices
- Write meaningful commit messages
- Add tests for new features
- Update documentation as needed

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Contact

**Hemanth Anuginti**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/hemanth-anuginti/)
[![Email](https://img.shields.io/badge/Email-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:hemanthanuginti@gmail.com)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/hemanth-chowdary-07)

---

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Backend framework
- [React](https://reactjs.org/) - Frontend library
- [Groq](https://groq.com/) - AI API provider
- [JavaParser](https://javaparser.org/) - AST parsing library
- [Render](https://render.com/) - Backend hosting
- [Vercel](https://vercel.com/) - Frontend hosting

---

## 📊 Project Stats

![GitHub stars](https://img.shields.io/github/stars/hemanth-chowdary-07/RefactorAI?style=social)
![GitHub forks](https://img.shields.io/github/forks/hemanth-chowdary-07/RefactorAI?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/hemanth-chowdary-07/RefactorAI?style=social)

---

<div align="center">

**⭐ If you found this project helpful, please consider giving it a star! ⭐**

Made with ❤️ by [Hemanth Anuginti](https://github.com/hemanth-chowdary-07)

</div>
