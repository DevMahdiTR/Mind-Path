# [Non-Typical Education Platform]

## 1. Project Overview

**Description**:  
This is a next-generation education platform designed to redefine e-learning experiences. It combines AI-powered personal assistance, gamification, and hyper-interactive features to provide a holistic and engaging learning journey. With robust system quality controls, users can easily manage classes, teachers, students, and subscriptions while ensuring top-tier educational standards.

**Inspiration**:  
The platform addresses the growing need for tailored, engaging, and scalable educational solutions. By integrating AI for personalized guidance and gamification for learner engagement, this project aims to make education more accessible, interactive, and impactful for students and educators worldwide.

---

## 2. Features

- **AI-Powered Personal Assistance**: Guides users to select the best courses and navigate the platform effectively.
- **Gamification with Hyper-Interactivity**: Keeps learners engaged through rewards, leaderboards, and dynamic feedback mechanisms.
- **Class Management**: Enables educators to create, manage, and organize classes seamlessly.
- **Teacher and Student Management**: Streamlines the process of managing teacher and student profiles with relevant permissions.
- **Subscription System**: Handles subscription fees and user access with automated invoicing and reminders.
- **System Quality Controller**: Monitors and maintains the quality of the platform’s functionality and content.

---

## 3. Technology Stack

- **Frontend**:  
  - React.js with TypeScript for a scalable and efficient user interface.
  - Additional tools: React Router, Redux Toolkit (state management), and Material-UI for design components.

- **Backend**:  
  - Spring Boot for a robust, scalable, and secure backend.
  - Additional libraries: Hibernate for ORM, Spring Security for authentication, and Spring Data JPA for database access.

- **Database**:  
  - PostgreSQL for relational data storage.

- **Cloud Storage**:  
  - Microsoft Azure for secure and scalable file storage (e.g., class materials, user uploads).

- **Other Tools**:  
  - Swagger for API documentation.
  - Axios for frontend-backend communication.
  - Postman for API testing.

---

## 4. Installation and Usage

### **Prerequisites**

- Node.js (version 16 or higher)
- npm (version 7 or higher)
- Java Development Kit (JDK) 17 or higher
- PostgreSQL (local or cloud instance)
- Azure Storage account setup

---

### **Installation Steps**

#### Backend Setup:

1. Clone the repository:
   ```bash
   https://github.com/DevMahdiTR/Mind-Path.git

```bash
  cd mindpath
```

Open your PostgreSQL client or terminal and run
```bash
CREATE DATABASE mindpathdb;
```
Open the file located at src/main/resources/application.yml and update the following
```bash
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mindpathdb
    username: your_postgres_username
    password: your_postgres_password
    driver-class-name: org.postgresql.Driver
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
server:
  port: 8083

```
Build and run the backend:
```bash
in some cases : mvn clean install

java -jar target/

./mvnw spring-boot:run
```
Frontend Setup:
Clone the repository:

bash
Copier
```bash
git clone https://github.com/DevMahdiTR/mindpath-front.git
```
Navigate to the frontend directory:

```bash
cd mindpath-front
```

Install dependencies:


```bash
npm install
```

Run the application:
```bash
npm start
```

4 . Usage Instructions
Access the application via the localhost URL provided by the frontend.
Log in as an administrator, teacher, or student using predefined credentials or by creating a new account.
Navigate to:
Classes Section: Create and manage classes.
User Management: Add or update teacher and student profiles.
Subscription Panel: Handle subscription fees and user payments.
AI Assistance: Get personalized guidance on courses and features.

5. Demo
Live Demo: https://drive.google.com/file/d/1wosQimZ7lWeE9A1cjQIpSu5EJvNbf-SD/view?usp=sharing
front hosted : https://mindpath-front.vercel.app/
backend hoster url : https://51.91.96.132:8083/api/v1

6. Team Members
Name 1: Mohamed Mahdi Trabelsi [Backend]
Name 2: Amir Chebbi [frontend]
Name 3: Saber Arfaoui [AI]

7. Challenges Faced

Integration of AI Assistance: Balancing personalized guidance without overwhelming users.
Solution: Simplified AI responses and user-specific recommendations.
Azure Storage Integration: Managing secure file uploads and retrievals.
Solution: Optimized API connections and error handling for file storage.
Gamification Design: Avoiding user fatigue with repetitive gamified elements.
Solution: Adaptive gamification based on user behavior.


8. Future Improvements
Mobile App: Launching a mobile version of the platform for enhanced accessibility.
Advanced Analytics: Providing users with detailed performance insights and progress tracking.
Multi-Language Support: Adding support for multiple languages to reach a global audience.


9. License

MIT License

Copyright (c) [2025] [RunTimeTerror/ Mind Path]

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated


10. Acknowledgments
Mentors and advisors for guidance during development.
Microsoft Azure for providing reliable cloud storage services.
Open-source tools and frameworks used in the project.

