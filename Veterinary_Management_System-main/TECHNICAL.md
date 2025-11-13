# 🔧 Technical Documentation - Veterinary Management System

Comprehensive technical documentation for developers working on the Veterinary Management System.

## 🏗️ Project Architecture

### **System Overview**
The Veterinary Management System follows a traditional **3-tier architecture** with clear separation of concerns:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │     Business    │    │      Data       │
│      Layer      │◄──►│     Logic       │◄──►│      Layer      │
│                 │    │                 │    │                 │
│  Thymeleaf     │    │   Spring Boot   │    │     MySQL       │
│  HTML/CSS/JS   │    │   Services      │    │   JPA/Hibernate │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### **Technology Stack**
- **Framework**: Spring Boot 3.x with Spring Security
- **Database**: MySQL 8.0+ with HikariCP connection pooling
- **ORM**: JPA/Hibernate for data persistence
- **Frontend**: Thymeleaf templates with vanilla JavaScript
- **Build Tool**: Maven for dependency management
- **Email**: Spring Mail with Gmail SMTP integration

### **Project Structure**
```
src/
├── main/
│   ├── java/com/vet/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST and MVC controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/      # Custom exception handlers
│   │   ├── model/          # JPA entities
│   │   ├── repository/     # Data access layer
│   │   ├── security/       # Security configuration
│   │   ├── service/        # Business logic layer
│   │   └── util/           # Utility classes
│   ├── resources/
│   │   ├── static/         # CSS, JS, images
│   │   ├── templates/      # Thymeleaf templates
│   │   └── application.properties
│   └── webapp/             # Web resources
└── test/                   # Test classes
```

## 🗄️ Database Schema

### **Core Tables**

#### **Users Table**
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    mobile VARCHAR(20),
    role ENUM('ADMIN', 'VETERINARIAN', 'OWNER') NOT NULL,
    specialization VARCHAR(100),
    working_hours TEXT,
    address TEXT,
    profile_photo VARCHAR(255),
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### **Pets Table**
```sql
CREATE TABLE pets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    age INT,
    gender ENUM('MALE', 'FEMALE', 'UNKNOWN'),
    weight DECIMAL(5,2),
    owner_id BIGINT NOT NULL,
    photo VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### **Appointments Table**
```sql
CREATE TABLE appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pet_id BIGINT NOT NULL,
    veterinarian_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    date_time DATETIME NOT NULL,
    reason TEXT,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED', 'RESCHEDULED') DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    FOREIGN KEY (veterinarian_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### **Prescriptions Table**
```sql
CREATE TABLE prescriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pet_id BIGINT NOT NULL,
    veterinarian_id BIGINT NOT NULL,
    medication TEXT NOT NULL,
    dosage TEXT NOT NULL,
    frequency TEXT NOT NULL,
    duration TEXT NOT NULL,
    instructions TEXT,
    prescribed_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    FOREIGN KEY (veterinarian_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### **Messages Table**
```sql
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    subject VARCHAR(200),
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### **Database Relationships**
```
Users (1) ──── (N) Pets
Users (1) ──── (N) Appointments (as owner)
Users (1) ──── (N) Appointments (as veterinarian)
Users (1) ──── (N) Prescriptions (as veterinarian)
Pets (1) ──── (N) Appointments
Pets (1) ──── (N) Prescriptions
Users (1) ──── (N) Messages (as sender)
Users (1) ──── (N) Messages (as receiver)
```

## 🔌 API Documentation

### **Authentication Endpoints**

#### **User Registration**
```http
POST /user/register
Content-Type: application/x-www-form-urlencoded

username=john_doe&email=john@example.com&password=secure123&fullName=John Doe&role=OWNER&mobile=+1234567890
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "redirectUrl": "/login"
}
```

#### **User Login**
```http
POST /login
Content-Type: application/x-www-form-urlencoded

username=john_doe&password=secure123
```

**Response:** Redirects to appropriate dashboard based on user role

### **Profile Management Endpoints**

#### **View Profile**
```http
GET /profile
Authorization: Bearer {JWT_TOKEN}
```

**Response:** Thymeleaf template with user profile data

#### **Update Profile Photo**
```http
POST /profile/photo
Content-Type: multipart/form-data
Authorization: Bearer {JWT_TOKEN}

profilePhotoFile: [binary file]
_csrf: {CSRF_TOKEN}
```

**Response:**
```json
{
  "success": true,
  "message": "Profile photo updated successfully",
  "photoUrl": "/uploads/profile-photos/uuid.jpg",
  "fileName": "uuid.jpg"
}
```

#### **Update Profile Information**
```http
POST /profile/edit
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer {JWT_TOKEN}

fullName=John Doe&email=john@example.com&mobile=+1234567890&_csrf={CSRF_TOKEN}
```

**Response:** Redirects to profile page with success message

### **Appointment Management Endpoints**

#### **Book Appointment**
```http
POST /owner/appointments/book
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer {JWT_TOKEN}

petId=1&veterinarianId=2&dateTime=2024-08-20T10:00:00&reason=Annual checkup&_csrf={CSRF_TOKEN}
```

**Response:**
```json
{
  "success": true,
  "message": "Appointment booked successfully! Confirmation emails have been sent.",
  "appointmentId": 123
}
```

#### **Get Appointments**
```http
GET /owner/appointments
Authorization: Bearer {JWT_TOKEN}
```

**Response:** Thymeleaf template with appointment list

#### **Reschedule Appointment**
```http
POST /owner/appointments/{id}/edit
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer {JWT_TOKEN}

dateTime=2024-08-21T14:00:00&reason=Rescheduled checkup&_csrf={CSRF_TOKEN}
```

**Response:** Redirects to appointments page with success message

### **Prescription Management Endpoints**

#### **Create Prescription**
```http
POST /vet/prescriptions/add
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer {JWT_TOKEN}

petId=1&medication=Amoxicillin&dosage=250mg&frequency=Twice daily&duration=7 days&instructions=Take with food&_csrf={CSRF_TOKEN}
```

**Response:** Redirects to prescriptions list with success message

#### **View Prescriptions**
```http
GET /vet/prescriptions
Authorization: Bearer {JWT_TOKEN}
```

**Response:** Thymeleaf template with prescription list

### **Admin Endpoints**

#### **User Management**
```http
GET /admin/users
Authorization: Bearer {JWT_TOKEN}
```

**Response:** Thymeleaf template with user management interface

#### **Edit User**
```http
GET /admin/users/{id}/edit
Authorization: Bearer {JWT_TOKEN}
```

**Response:** Thymeleaf template with user edit form

```http
POST /admin/users/{id}/edit
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer {JWT_TOKEN}

fullName=Updated Name&email=updated@example.com&_csrf={CSRF_TOKEN}
```

**Response:** Redirects to users list with success message

## 🔐 Security Implementation

### **Authentication & Authorization**

#### **Password Security**
- **Hashing**: BCrypt with configurable strength (default: 10 rounds)
- **Salt**: Automatically generated and stored with hash
- **Validation**: Minimum 8 characters, complexity requirements

#### **Session Management**
- **CSRF Protection**: Enabled for all state-changing operations
- **Session Timeout**: Configurable (default: 30 minutes)
- **Concurrent Sessions**: Limited to prevent session hijacking

#### **Role-Based Access Control**
```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasRole('VETERINARIAN')")
@PreAuthorize("hasRole('OWNER')")
@PreAuthorize("isAuthenticated()")
```

### **Input Validation**

#### **Server-Side Validation**
- **Bean Validation**: JSR-303 annotations (@NotNull, @Email, @Size)
- **Custom Validators**: Business logic validation
- **SQL Injection Prevention**: Parameterized queries via JPA

#### **Client-Side Validation**
- **HTML5 Validation**: Required fields, email format, file types
- **JavaScript Validation**: Real-time feedback and error handling
- **CSRF Token**: Included in all forms and AJAX requests

### **File Upload Security**

#### **Validation**
- **File Type**: Whitelist of allowed MIME types
- **File Size**: Configurable maximum (default: 5MB)
- **File Name**: Sanitized to prevent path traversal
- **Storage**: Isolated upload directory with proper permissions

#### **Implementation**
```java
@Service
public class FileStorageServiceImpl implements FileStorageService {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Override
    public String storeFile(MultipartFile file, String directory) {
        // Validate file type and size
        // Generate unique filename
        // Store in secure location
        // Return filename for database storage
    }
}
```

## 📧 Third-Party Services Integration

### **Email Service (Gmail SMTP)**

#### **Configuration**
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### **Implementation**
```java
@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void sendEmailWithRetry(String to, String subject, String content, int maxRetries) {
        // Implement retry logic with exponential backoff
        // Handle failures gracefully
        // Log all email operations
    }
}
```

#### **Email Templates**
- **Appointment Confirmation**: Immediate confirmation upon booking
- **Appointment Reminder**: Scheduled reminders (8 AM daily)
- **System Notifications**: Important system updates

### **File Storage Service**

#### **Local File System**
- **Directory Structure**: Organized by file type and date
- **File Naming**: UUID-based to prevent conflicts
- **Access Control**: Web-accessible with proper security
- **Backup Strategy**: Regular backups of upload directory

#### **Implementation**
```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get("./uploads").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
```

## 🚀 Deployment & Operations

### **Environment Configuration**

#### **Development Environment**
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/vet_management_dev
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Email (Development)
spring.mail.host=localhost
spring.mail.port=1025
spring.mail.username=
spring.mail.password=

# Logging
logging.level.com.vet=DEBUG
logging.level.org.springframework.security=DEBUG
```

#### **Production Environment**
```properties
# Database
spring.datasource.url=jdbc:mysql://prod-db:3306/vet_management
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Email (Production)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}

# Logging
logging.level.com.vet=INFO
logging.level.org.springframework.security=WARN
```

### **Build & Deployment**

#### **Maven Build**
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package -DskipTests

# Run with profile
mvn spring-boot:run -Dspring.profiles.active=prod
```

#### **Docker Deployment**
```dockerfile
FROM openjdk:17-jre-slim
VOLUME /tmp
COPY target/vet-management-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

#### **Environment Variables**
```bash
# Database
DB_HOST=prod-db.example.com
DB_PORT=3306
DB_NAME=vet_management
DB_USERNAME=vet_user
DB_PASSWORD=secure_password

# Email
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=vetcare@example.com
EMAIL_PASSWORD=app_password

# Application
SERVER_PORT=8080
FILE_UPLOAD_DIR=/app/uploads
```

### **Monitoring & Logging**

#### **Application Metrics**
- **Health Checks**: Spring Boot Actuator endpoints
- **Performance Monitoring**: Request timing and database queries
- **Error Tracking**: Comprehensive error logging with stack traces

#### **Log Management**
```properties
# Logging Configuration
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.file.name=logs/vet-management.log
logging.file.max-size=10MB
logging.file.max-history=30
```

## 🧪 Testing Strategy

### **Unit Testing**
- **Service Layer**: Business logic validation
- **Repository Layer**: Data access testing
- **Utility Classes**: Helper function testing

### **Integration Testing**
- **Controller Testing**: Endpoint validation
- **Database Integration**: JPA operations testing
- **Email Service**: SMTP integration testing

### **End-to-End Testing**
- **User Workflows**: Complete user journey testing
- **Cross-browser Testing**: Browser compatibility validation
- **Mobile Testing**: Responsive design validation

## 🐛 Known Issues & TODOs

### **Current Issues**
1. **Photo Upload**: Occasional CSRF token validation failures
2. **Email Delivery**: Gmail rate limiting for high-volume sending
3. **Mobile Responsiveness**: Some forms need mobile optimization

### **Planned Improvements**
1. **Real-time Notifications**: WebSocket implementation for live updates
2. **Advanced Search**: Full-text search for appointments and users
3. **Reporting System**: Analytics and reporting dashboard
4. **API Versioning**: REST API versioning for external integrations
5. **Multi-language Support**: Internationalization (i18n)

### **Performance Optimizations**
1. **Database Indexing**: Optimize query performance
2. **Caching**: Redis integration for frequently accessed data
3. **Image Optimization**: Automatic image compression and resizing
4. **CDN Integration**: Content delivery network for static assets

## 📚 Additional Resources

### **Documentation**
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/site/docs/current/reference/html5/)
- [Hibernate User Guide](https://hibernate.org/orm/documentation/6.0/)

### **Development Tools**
- **IDE**: IntelliJ IDEA, Eclipse, VS Code
- **Database**: MySQL Workbench, DBeaver
- **API Testing**: Postman, Insomnia
- **Version Control**: Git with GitHub/GitLab

### **Community & Support**
- **Spring Community**: [spring.io/community](https://spring.io/community)
- **Stack Overflow**: Tagged with `spring-boot`, `spring-security`
- **GitHub Issues**: Project-specific issue tracking

---

**Last Updated**: August 2024  
**Version**: 1.0.0  
**Maintainer**: [Your Name]  
**Contact**: [your-email@domain.com]
