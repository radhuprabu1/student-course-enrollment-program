package com.example.enrollment.course;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * @author Radhakrishnan
 */
@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Student Course Enrollment Project",
				description = "Student Course Registration Spring Boot Project",
				version = "v1.0",
				contact = @Contact(
						name = "Radhakrishnan",
						email = "radhumahadev@gmail.com"
						)
				)
		)
public class StudentCourseRegistrationProgramApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentCourseRegistrationProgramApplication.class, args);
	}
}
