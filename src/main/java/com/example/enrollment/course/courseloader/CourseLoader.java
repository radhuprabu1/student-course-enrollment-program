package com.example.enrollment.course.courseloader;

import java.io.InputStream;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.enrollment.course.entity.Course;
import com.example.enrollment.course.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CourseLoader implements CommandLineRunner {

	private final CourseRepository courseRepository;
	// Spring Boot auto-configures objectMapper.
	private final ObjectMapper objectMapper; 

	@Override
	public void run(String... args) throws Exception {

		// ClassPathResource - Spring utility class. It finds a file located
		// inside src/main/resources and returns the path.
		ClassPathResource resource = new ClassPathResource("courses.json");

		// InputStream -> Reads the content of the file.
		// resource.getInputStream() - opens the file for reading.
		try (InputStream inputStream = resource.getInputStream()) {
			// objectMapper.readValue(...) — reads the JSON data from the inputStream 
			// and converts it into a Java object.
			// new TypeReference<Set<Course>>() {} -> deserializing json and returns a train list.
			Set<Course> courses = objectMapper.readValue(inputStream, new TypeReference<Set<Course>>() {});
			for (Course course : courses) {
				courseRepository.save(course);
			}
		}
	}
}