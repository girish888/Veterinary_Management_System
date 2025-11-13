# 🏥 Veterinary Management System

A comprehensive full-stack web application designed to streamline veterinary care management, facilitating seamless communication and appointment scheduling between veterinarians and pet owners.

## 🌟 Key Features

### **User Management & Authentication**
- **Multi-role Support**: Separate portals for Veterinarians, Pet Owners, and Administrators
- **Secure Authentication**: Spring Security with BCrypt password hashing
- **Profile Management**: Complete user profiles with photo upload capabilities
- **Role-based Access Control**: Tailored interfaces for each user type

### **Appointment Management**
- **Smart Scheduling**: Real-time appointment booking with conflict detection
- **Immediate Confirmations**: Instant email confirmations upon booking
- **Automated Reminders**: Scheduled email reminders (8 AM daily)
- **Rescheduling**: Flexible appointment modification for pet owners

### **Communication & Notifications**
- **Email System**: Gmail SMTP integration for reliable delivery
- **Automated Reminders**: Configurable reminder scheduling
- **Real-time Updates**: Instant notifications for all system events

### **Profile & Media Management**
- **Photo Upload**: Drag-and-drop profile photo management
- **Image Validation**: Automatic file type and size validation
- **Responsive Design**: Mobile-optimized photo display
- **Fallback Support**: Graceful degradation for failed image loads

### **Dashboard & Analytics**
- **Role-specific Dashboards**: Customized views for each user type
- **Appointment Overview**: Comprehensive scheduling management
- **Profile Statistics**: User activity and information tracking

### **Admin Panel**
- **User Management**: Complete user oversight and control
- **System Monitoring**: Appointment and user analytics
- **Content Management**: System-wide configuration options

## 🛠️ Technologies Used

### **Frontend**
- **HTML5**: Semantic markup with modern standards
- **CSS3**: Advanced styling with CSS Grid, Flexbox, and custom properties
- **JavaScript (ES6+)**: Modern JavaScript with async/await and ES6 modules
- **Thymeleaf**: Server-side templating engine
- **Font Awesome**: Comprehensive icon library

### **Backend**
- **Spring Boot 3.x**: Modern Java framework with auto-configuration
- **Spring Security**: Robust authentication and authorization
- **Spring Data JPA**: Object-relational mapping with Hibernate
- **Spring Mail**: Email service integration
- **Spring Quartz**: Scheduled task management

### **Database**
- **MySQL 8.0+**: Reliable relational database
- **HikariCP**: High-performance connection pooling
- **JPA/Hibernate**: Object-relational mapping

### **Infrastructure**
- **Maven**: Dependency management and build automation
- **Gmail SMTP**: Email delivery service
- **File Storage**: Local file system with organized directory structure

## 🚀 Installation & Setup

### **Prerequisites**
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Modern web browser (Chrome, Firefox, Safari, Edge)

### **Step 1: Clone the Repository**
```bash
git clone https://github.com/yourusername/veterinary-management-system.git
cd veterinary-management-system
```

### **Step 2: Database Setup**
1. **Create MySQL Database**
   ```sql
   CREATE DATABASE vet_management;
   USE vet_management;
   ```

2. **Configure Database Connection**
   - Edit `src/main/resources/application.properties`
   - Update database credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/vet_management
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### **Step 3: Email Configuration**
1. **Generate Gmail App Password**
   - Go to Google Account Settings → Security
   - Enable 2-Factor Authentication
   - Generate App Password for "Mail"

2. **Update Email Settings**
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   ```

### **Step 4: Build and Run**
```bash
# Clean and build the project
mvn clean package

# Run the application
java -jar target/vet-management-0.0.1-SNAPSHOT.jar
```

### **Step 5: Access the Application**
- **Main Application**: http://localhost:7055
- **Admin Portal**: http://localhost:7055/admin
- **Veterinarian Portal**: http://localhost:7055/vet
- **Pet Owner Portal**: http://localhost:7055/owner

## 📱 User Guide

### **For Pet Owners**
1. **Registration**: Create account with email and basic information
2. **Profile Setup**: Upload profile photo and complete personal details
3. **Book Appointments**: Select veterinarian, date, and time
4. **Manage Pets**: Add and maintain pet information
5. **View History**: Access appointment and prescription history

### **For Veterinarians**
1. **Professional Profile**: Set up specialization and working hours
2. **Appointment Management**: View and manage scheduled appointments
3. **Prescription Management**: Create and manage pet prescriptions
4. **Communication**: Respond to pet owner messages

### **For Administrators**
1. **User Management**: Oversee all system users
2. **System Monitoring**: Track appointments and system usage
3. **Content Management**: Manage system-wide settings

## 🔧 Configuration

### **Environment Variables**
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/vet_management
spring.datasource.username=your_username
spring.datasource.password=your_password

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# File Upload
file.upload-dir=./uploads

# Server Configuration
server.port=7055
```

### **Customization Options**
- **Reminder Timing**: Configure email reminder schedules
- **File Upload Limits**: Adjust maximum file sizes
- **UI Themes**: Customize color schemes and layouts

## 🧪 Testing

### **Manual Testing**
1. **User Registration**: Test all user role registrations
2. **Photo Upload**: Verify image upload and display
3. **Appointment Booking**: Test scheduling and confirmation flow
4. **Email Delivery**: Confirm notification delivery

### **Browser Compatibility**
- ✅ Chrome 90+
- ✅ Firefox 88+
- ✅ Safari 14+
- ✅ Edge 90+

## 📸 Screenshots

*[Screenshots will be added here showing the main interfaces]*

## 🚨 Troubleshooting

### **Common Issues**

#### **Database Connection Failed**
```bash
# Check MySQL service status
sudo systemctl status mysql

# Verify credentials in application.properties
# Ensure database exists and is accessible
```

#### **Email Not Sending**
```bash
# Verify Gmail App Password
# Check firewall settings
# Ensure 2FA is enabled on Gmail account
```

#### **Photo Upload Issues**
```bash
# Check file permissions on uploads directory
# Verify file size limits
# Ensure proper file types (JPG, PNG, GIF)
```

### **Logs and Debugging**
- **Application Logs**: Check console output for detailed error messages
- **Database Logs**: Review MySQL error logs for connection issues
- **Email Debug**: Enable debug logging in application.properties

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### **Development Setup**
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

### **Getting Help**
- **Documentation**: Check this README and TECHNICAL.md
- **Issues**: Report bugs via GitHub Issues
- **Discussions**: Join our community discussions

### **Contact Information**
- **Project Maintainer**: [Your Name]
- **Email**: [your-email@domain.com]
- **GitHub**: [@yourusername]

## 🙏 Acknowledgments

- **Spring Boot Team**: For the excellent framework
- **MySQL Community**: For the reliable database
- **Font Awesome**: For the comprehensive icon library
- **Open Source Community**: For inspiration and support

---

**Made with ❤️ for the veterinary community**

*Last updated: August 2024*
#   V e t e r i n a r y _ M a n a g e m e n t _ S y s t e m  
 