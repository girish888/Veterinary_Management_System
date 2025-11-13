🏥 Veterinary Management System

A complete web application designed to manage veterinary clinic operations.
The system supports Pet Owners, Veterinarians, and Administrators by simplifying appointments, pet details, communication, notifications, and daily clinic workflows.

⭐ Key Features
🔐 User Accounts & Roles

Three user types: Admin, Veterinarian, Pet Owner

Secure login using Spring Security

Passwords protected with BCrypt encryption

Individual dashboards based on role

📅 Appointment System

Book appointments with veterinarians

Automatic time-slot conflict detection

Instant email confirmation after booking

Daily 8 AM reminder emails

Easy rescheduling options

📬 Email & Notifications

Gmail SMTP-based email service

Appointment confirmations

Reminder notifications

Real-time updates for key actions

🖼️ Profile & Photo Management

Upload profile images

File type & size validation

Responsive photo display

Fallback images if photo fails to load

📊 Dashboards

Admin: user management, analytics, appointments

Veterinarian: daily schedule, prescriptions, messages

Pet Owner: pet profiles, bookings, history

Clean analytics & statistics

🛠️ Admin Controls

Add/edit/remove users

Monitor system-wide appointments

Update system settings

View analytical data

🧰 Technologies Used

Frontend: HTML, CSS, JavaScript, Thymeleaf

Backend: Spring Boot, Spring Security, Spring Data JPA

Database: MySQL

Email: Spring Mail (Gmail SMTP)

Scheduler: Quartz

Build Tool: Maven

🚀 How to Run the Project
1️⃣ Install Requirements

Java 17+

MySQL 8+

Maven

2️⃣ Database Setup

Create MySQL database:

CREATE DATABASE vet_management;

3️⃣ Update application.properties

Add your DB & email credentials.

4️⃣ Build & Run
mvn clean package
java -jar target/vet-management-0.0.1-SNAPSHOT.jar

5️⃣ Access Application

Main: http://localhost:7055

Admin: http://localhost:7055/admin

Veterinarian: http://localhost:7055/vet

Owner: http://localhost:7055/owner

📱 User Functions
👤 Pet Owners

Manage profile & pets

Book appointments

Receive automated reminders

Access past appointments

👨‍⚕️ Veterinarians

View/manage appointments

Handle pet medical details

Create prescriptions

Communicate with owners

🛡️ Admins

Manage entire system

Control user data & permissions

Monitor appointments

View stats & reports

🧪 Testing Areas

User registration & login

Appointment booking flow

Email notifications

Photo upload

Dashboard functionality

❗ Troubleshooting

MySQL not starting → check DB service

Emails not working → verify Gmail App Password

Image upload issues → check uploads/ directory permissions

This project was developed as part of Project (203105400),
7th Semester,
B.Tech CSE, Parul University, Academic Year 2023–2024,
under Group No. AI-96.

👨‍💻 Team Members (Updated Roles)
Name	Enrollment No.	Role

Y. Girish Uday	2203031241436	Backend Developer + Spring Boot + GitHub Maintainer

M. Devendra	2203031240761	Backend Developer + Spring Boot

N. Vijay Kumar	2203031240939	Tester

S. Leela Prasad	2203031241190	Frontend Developer

🧑‍🏫 Project Guide

Mr. Hojiwala Robin AjayKumar
Project Guide, Parul University

📘 Project Coordinators

Dr. Mohammad Arif

Dr. Sanjay Agal
Head of CSE, PIET, Parul University

📄 License

This project is licensed under the MIT License.
