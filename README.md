# Student Course Enrollment Program - Spring Boot Project

[![Java](https://img.shields.io/badge/Java-17-blue?logo=java)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/SpringBoot-3-green?logo=spring)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Build-Maven-blue?logo=apachemaven)](https://maven.apache.org/)
[![Status](https://img.shields.io/badge/Database-InMemory-yellow)](#)
[![Tested With](https://img.shields.io/badge/Tested%20With-Postman-orange?logo=postman)](https://www.postman.com/)

## 📋 Project Overview
This is a simple yet powerful **Student Course Enrollment REST API Application**.  
Built using **Java**, **Spring Boot**, and **Spring Data Jpa** to connect with external MySql Database Server.
All operations (search, create student, enroll, delete, deregister) are tested using **Postman** client and also implemented **Swagger url**

---

## 🛠️ Technologies Used (Tech Stack)
- ⚙️ Java 17
- 🌱 Spring Boot 3.x
- 📦 Maven (Dependency Management)
- 💾 Spring Data Jpa (connects with MySql server) for data storage
- 🔄 RESTful APIs
- 🧪 Postman (for API testing)
- 🧰 Eclipse IDE

---

## 🚀 Main Features

- 🔍 **List Courses**: Retrieves and lists all the courses offered in the system
- 👤 **Add Student**: Creates and adds a new student in the system 
- 🎟️ **Enroll Student**: Enrolls student to the given course in the system
- 📄 **List Student-Courses**: Retrieves and lists all the courses that a student enrolled
- ❌ **Deregister Course**: Deregisters the given course from the student in the system
- ❌ **Deregister Student**: Deregisters all the courses associated with the given student and deletes student from the system
- 🎟️ **Swap Courses**: Students can swap between courses available in the system  
- 📄️ **List Students**: Retrieves and lists all student records from the system 

All features are accessible via **REST endpoints**, designed with clean coding principles, layered architecture, and extensibility in mind.

---

## 📚 API Endpoints

|HTTP Method|					Endpoint						|				Purpose								 |
|-----------|---------------------------------------------------|------------------------------|
|	GET		| `/api/v1/courses`									|	Search available courses							  |
|	POST	| `/api/v1/students`								|	Creates a new student in the system					  |
|	POST	| `/api/v1/enroll/{courseId}/student/{studentId}`	|	Enrolls a student to a course						 |
|	GET		| `/api/v1/enrollments/student/{studentId}`			|	Retrieves all the enrolled courses of a Student	|
|	PATCH	| `/api/v1/swap/{studentId}`						|	Swap Between Available Courses						 |
|	PUT	| `/api/v1/deregister/{courseId}/student/{studentId}`	|	Deregisters course associated with Student		 |
|	DELETE	| `/api/v1/students/{studentId}`					|	Deletes Student from the system						  |
|	GET		| `/api/v1/students`								|	Retrieves all the Students from the system		|

---

## 🧩 Project Structure

```
com.example.enrollment.course
├── controller       // All REST API endpoints
├── dto              // DTO objects for presenting to the end user
├── exception        // Exception handlers for errors and invalid user input
├── mapper           // Converts model objects to DTO objects and vice-versa
├── model            // POJOs representing Trains, Travellers, Tickets
├── repository       // In-memory data storage using Java Collections
├── service          // Business Logic Layer
├── courseLoader     // Creates a course list using CommandLineRunner
└── StudentCourseRegistrationProgramApplication.java  // Main Spring Boot Application
```

---

## 👨‍💻 How to Run the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/radhuprabu1/student-course-enrollment-program.git
   cd student-course-enrollment-program
   ```

2. Build and run the Spring Boot application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. Test APIs using Postman or any REST client.

---

## 💼 Project Highlights

✅ Demonstrates practical use of:
- Spring Boot REST architecture  
- In-memory data repositories using Java collection framework (`HashMap`, `Queue`, `Set`, `List`) 
- Layered coding practices (Controller, Service, Repository)  
- DTO pattern with mapping logic  
- Exception handling using `@ControllerAdvice`  
- JUnit & Mockito for unit testing  
- Clean code with SOLID principles and Java best practices
- Added Swagger UI and documented Structure

---

## 📧 Contact

If you found this project useful or want to collaborate, feel free to connect!

📩 Email: radhumahadev@gmail.com  

---

## 📌 Future Enhancements

- 🛡️ Add Spring-Security based authentication for users - admins, students,...  

---

⭐️ **If you like this project, please give it a star on GitHub to show support!**
