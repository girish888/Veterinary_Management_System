🏥 Veterinary Management System

A complete web application designed to manage all veterinary clinic activities. The system helps Pet Owners, Veterinarians, and Admins handle appointments, pets, profiles, and communication easily.

⭐ Key Features
🔐 User Accounts & Roles

Three user types: Admin, Veterinarian, Pet Owner

Secure login using Spring Security

Passwords stored safely using BCrypt

Each user gets their own dashboard

📅 Appointment System

Pet owners can book appointments with veterinarians

System checks for time conflicts

Email sent instantly after booking

Automatic daily reminder emails at 8 AM

Users can reschedule appointments easily

📬 Email & Notifications

Email service integrated using Gmail SMTP

Appointment confirmation emails

Reminder emails for upcoming appointments

Notification messages for all important actions

🖼️ Profile & Photo Management

Users can upload profile photos

System checks image size and type

Photos display properly on all devices

Uses fallback image if photo fails to load

📊 Dashboards

Admin Dashboard: Manage users, view system data

Veterinarian Dashboard: View appointments, write prescriptions

Pet Owner Dashboard: Manage pets and appointments

Shows stats and important information in one place

🛠️ Admin Controls

Add, edit, or remove users

Monitor appointments

Control system settings

View analytics and data

🧰 Technologies Used

Frontend: HTML, CSS, JavaScript, Thymeleaf

Backend: Spring Boot, Spring Security, Spring Data JPA

Database: MySQL

Email: Spring Mail (Gmail SMTP)

Scheduler: Quartz for automated tasks

Build Tool: Maven

🚀 How to Run

Install Java 17, MySQL, and Maven

Create database vet_management

Update DB and Email settings in application.properties

Run:

mvn clean package
java -jar target/vet-management-0.0.1-SNAPSHOT.jar


Open the app:

Main: http://localhost:7055

Admin: http://localhost:7055/admin

Vet: http://localhost:7055/vet

Owner: http://localhost:7055/owner

📱 User Functions
👤 Pet Owners

Create account

Manage profile and pets

Book and view appointments

Get email notifications

👨‍⚕️ Veterinarians

Manage daily appointments

View pet details

Write prescriptions

Communicate with owners

🛡️ Admin

Full control of system

Manage all users

View analytics

Maintain system records

🧪 Testing Areas

Registration and Login

Appointment booking

Email sending

Image upload

Dashboard navigation

❗ Troubleshooting

Check MySQL connection if app doesn't start

Verify Gmail app password for email errors

Ensure uploads/ folder has permission for photo upload

📄 License

This project uses the MIT License.
