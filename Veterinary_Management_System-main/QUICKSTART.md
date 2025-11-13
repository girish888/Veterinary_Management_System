# 🚀 Quick Start Guide - Veterinary Management System

Get up and running with the Veterinary Management System in under 10 minutes!

## ⚡ Prerequisites Check

Before you start, ensure you have:
- ✅ Java 17+ installed
- ✅ MySQL 8.0+ running
- ✅ Maven 3.6+ installed
- ✅ Git installed

## 🎯 5-Minute Setup

### **Step 1: Clone & Navigate**
```bash
git clone https://github.com/yourusername/veterinary-management-system.git
cd veterinary-management-system
```

### **Step 2: Database Setup**
```sql
-- Connect to MySQL and run:
CREATE DATABASE vet_management;
USE vet_management;
```

### **Step 3: Configure Application**
Edit `src/main/resources/application.properties`:
```properties
# Update these values:
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.mail.username=your_gmail@gmail.com
spring.mail.password=your_gmail_app_password
```

### **Step 4: Build & Run**
```bash
mvn clean package
java -jar target/vet-management-0.0.1-SNAPSHOT.jar
```

### **Step 5: Access Application**
Open your browser and go to: **http://localhost:7055**

## 🔑 Default Login Credentials

### **Admin Account**
- **Username**: `admin`
- **Password**: `admin123`
- **Access**: http://localhost:7055/admin

### **Create Test Users**
1. **Register as Pet Owner**: http://localhost:7055/user/register
2. **Register as Veterinarian**: http://localhost:7055/user/register

## 🧪 Quick Test Scenarios

### **Test 1: User Registration**
1. Go to `/user/register`
2. Fill out the form with test data
3. Verify account creation

### **Test 2: Profile Photo Upload**
1. Login to your account
2. Go to `/profile`
3. Click on profile photo area
4. Upload an image file
5. Verify photo displays correctly

### **Test 3: Appointment Booking**
1. Login as Pet Owner
2. Go to `/owner/appointments`
3. Click "Book New Appointment"
4. Fill appointment details
5. Verify confirmation email

### **Test 4: Email System**
1. Check console logs for email sending
2. Verify email configuration in `application.properties`
3. Test with Gmail App Password

## 🐛 Common Issues & Quick Fixes

### **Issue: Database Connection Failed**
```bash
# Check MySQL service
sudo systemctl status mysql

# Verify credentials in application.properties
# Ensure database exists
```

### **Issue: Email Not Sending**
```bash
# Verify Gmail App Password
# Check 2FA is enabled
# Ensure correct SMTP settings
```

### **Issue: Photo Upload Fails**
```bash
# Check uploads directory permissions
# Verify file size limits (5MB max)
# Ensure proper file types (JPG, PNG, GIF)
```

### **Issue: Maven Build Fails**
```bash
# Clean Maven cache
mvn clean

# Update dependencies
mvn clean package -U

# Check Java version
java -version
```

## 📁 Key Files & Directories

### **Configuration Files**
- `application.properties` - Main configuration
- `pom.xml` - Maven dependencies
- `src/main/java/com/vet/config/` - Java configuration classes

### **Main Controllers**
- `HomeController.java` - Public pages
- `UserController.java` - User management
- `ProfileController.java` - Profile operations
- `AppointmentController.java` - Appointment management

### **Templates**
- `src/main/resources/templates/` - Thymeleaf HTML templates
- `src/main/resources/static/` - CSS, JavaScript, images

### **Database**
- `src/main/resources/data.sql` - Initial data
- `src/main/java/com/vet/model/` - JPA entities

## 🔧 Development Workflow

### **1. Make Changes**
```bash
# Edit source files
# Modify templates
# Update CSS/JavaScript
```

### **2. Test Changes**
```bash
# Restart application
# Test in browser
# Check console logs
```

### **3. Build & Deploy**
```bash
# Package application
mvn clean package

# Run with new version
java -jar target/vet-management-0.0.1-SNAPSHOT.jar
```

## 📚 Next Steps

### **For New Developers**
1. **Read the Code**: Start with `HomeController.java` and follow the flow
2. **Understand Models**: Review JPA entities in `model/` package
3. **Explore Services**: Check business logic in `service/` package
4. **Study Templates**: Look at Thymeleaf templates for UI structure

### **For Contributors**
1. **Fork the Repository**: Create your own fork
2. **Create Feature Branch**: `git checkout -b feature/your-feature`
3. **Make Changes**: Implement your feature
4. **Test Thoroughly**: Ensure all functionality works
5. **Submit PR**: Create pull request with description

### **For Deployment**
1. **Environment Setup**: Configure production environment variables
2. **Database Migration**: Set up production database
3. **Email Configuration**: Configure production email service
4. **Security Review**: Ensure proper security measures
5. **Monitoring**: Set up logging and monitoring

## 🆘 Need Help?

### **Quick Resources**
- **README.md** - User-facing documentation
- **TECHNICAL.md** - Developer documentation
- **Console Logs** - Check application output
- **Browser DevTools** - Frontend debugging

### **Community Support**
- **GitHub Issues**: Report bugs and request features
- **Stack Overflow**: Search for Spring Boot solutions
- **Spring Community**: Official Spring support

---

**Happy Coding! 🎉**

*This guide gets you started in minutes. For detailed information, see README.md and TECHNICAL.md*
