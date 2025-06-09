package com.example.enrollment.course.controller.bot;

import java.util.Collection;
import java.util.List;

import org.springframework.ai.openai.client.chat.OpenAIChatClient;
import org.springframework.ai.openai.client.chat.OpenAIChatRequest;
import org.springframework.ai.openai.client.chat.OpenAIChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.enrollment.course.dto.CourseDto;
import com.example.enrollment.course.dto.EnrollmentDto;
import com.example.enrollment.course.dto.StudentDto;
import com.example.enrollment.course.dto.bot.ChatRequest;
import com.example.enrollment.course.entity.enums.EnrollmentStatus;
import com.example.enrollment.course.exception.ResourceNotFoundException;
import com.example.enrollment.course.service.course.CourseService;
import com.example.enrollment.course.service.enrollment.EnrollmentService;
import com.example.enrollment.course.service.student.StudentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * ChatController provides a single /chat endpoint.
 * Users send a natural‐language message, and Spring AI (OpenAI) returns a JSON intent.
 * We then invoke the existing CourseService / StudentService / EnrollmentService accordingly.
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final OpenAIChatClient chatClient;         // Spring AI 1.0.0
    private final CourseService courseService;
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.ai.openai.chat.model:gpt-3.5-turbo}")
    private String chatModel;

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        String userMessage = request.getMessage();

        // 1) Build a system + user prompt asking for JSON with a specific structure.
        String prompt = """
            You are an assistant for a student-course registration system.
            When given the user's message, respond with a JSON object that includes:
            
            {
              "intent": one of [ "LIST_COURSES", "REGISTER_STUDENT",
                                 "ENROLL_COURSE", "DEREGISTER_COURSE",
                                 "SWAP_COURSE", "GET_ENROLLMENTS",
                                 "DEREGISTER_STUDENT", "UNKNOWN" ],
              
              // For REGISTER_STUDENT:
              "firstName": "...",
              "lastName": "...",
              "contactNumber": "...",
              
              // For ENROLL_COURSE or DEREGISTER_COURSE:
              "studentId": 123,
              "courseId": 456,
              
              // For SWAP_COURSE:
              "studentId": 123,
              "oldCourseId": 111,
              "newCourseId": 222,
              
              // For GET_ENROLLMENTS or DEREGISTER_STUDENT:
              "studentId": 123
            }
            Do NOT include any additional keys. Do NOT reply in plain text—ONLY return valid JSON.
            
            User message: "%s"
            """.formatted(userMessage);

        // 2) Call OpenAI via Spring AI 1.0.0
        OpenAIChatRequest chatReq = OpenAIChatRequest.builder()
            .model(chatModel)
            .messages(List.of(
                // System role: instruct the model
                OpenAIChatRequest.ChatMessage.ofSystem(
                    "You are a student-course registration assistant."
                ),
                // User role: the actual directive
                OpenAIChatRequest.ChatMessage.ofUser(prompt)
            ))
            .build();

        OpenAIChatResponse aiResponse = chatClient.call(chatReq);
        String aiContent = aiResponse.getChoices().get(0).getMessage().getContent().trim();

        // 3) Parse the returned JSON
        JsonNode root;
        try {
            root = objectMapper.readTree(aiContent);
        } catch (Exception e) {
            return ResponseEntity.ok(
                "Error: could not parse AI response. Please rephrase your request."
            );
        }

        String intent = root.path("intent").asText("UNKNOWN");

        try {
            switch (intent) {
                case "LIST_COURSES" -> {
                    Collection<CourseDto> courses = courseService.searchCourses();
                    if (courses.isEmpty()) {
                        return ResponseEntity.ok("No courses available at the moment.");
                    }
                    StringBuilder sb = new StringBuilder("Available courses:\n");
                    for (CourseDto c : courses) {
                        sb.append("- ID: ")
                          .append(c.getCourseId())
                          .append(", Name: ")
                          .append(c.getCourseName())
                          .append("\n");
                    }
                    return ResponseEntity.ok(sb.toString());
                }

                case "REGISTER_STUDENT" -> {
                    String firstName = root.path("firstName").asText("");
                    String lastName = root.path("lastName").asText("");
                    String contact = root.path("contactNumber").asText("");

                    if (firstName.isBlank() || contact.isBlank()) {
                        return ResponseEntity.ok(
                            "Missing required fields. Please include firstName and contactNumber."
                        );
                    }

                    StudentDto newStudent = new StudentDto(
                        null, firstName, lastName, contact
                    );
                    StudentDto saved = studentService.addStudent(newStudent);
                    return ResponseEntity.ok(
                        "Student registered successfully with ID: " 
                        + saved.getStudentId()
                    );
                }

                case "ENROLL_COURSE" -> {
                    long sId = root.path("studentId").asLong(-1);
                    long cId = root.path("courseId").asLong(-1);
                    if (sId < 0 || cId < 0) {
                        return ResponseEntity.ok(
                            "Missing studentId or courseId for enrollment."
                        );
                    }
                    EnrollmentDto enrollment = enrollmentService.enrollStudent(cId, sId);
                    if (enrollment.getStatus() == EnrollmentStatus.ENROLLED) {
                        return ResponseEntity.ok(
                            "Enrollment successful: Enrollment ID " 
                            + enrollment.getEnrollmentId()
                        );
                    } else {
                        return ResponseEntity.ok(
                            "Course is full; you have been waitlisted at position " 
                            + enrollment.getWaitlistPosition()
                        );
                    }
                }

                case "DEREGISTER_COURSE" -> {
                    long sId = root.path("studentId").asLong(-1);
                    long cId = root.path("courseId").asLong(-1);
                    if (sId < 0 || cId < 0) {
                        return ResponseEntity.ok(
                            "Missing studentId or courseId for deregistration."
                        );
                    }
                    enrollmentService.deregisterCourse(cId, sId);
                    return ResponseEntity.ok(
                        "Deregistered student " + sId + " from course " + cId
                    );
                }

                case "SWAP_COURSE" -> {
                    long sId     = root.path("studentId").asLong(-1);
                    long oldCID  = root.path("oldCourseId").asLong(-1);
                    long newCID  = root.path("newCourseId").asLong(-1);
                    if (sId < 0 || oldCID < 0 || newCID < 0) {
                        return ResponseEntity.ok(
                            "Missing studentId, oldCourseId, or newCourseId."
                        );
                    }
                    EnrollmentDto updated = 
                        enrollmentService.updateCourseEnrollment(sId, oldCID, newCID);
                    return ResponseEntity.ok(
                        "Swapped enrollment. New Enrollment ID: " 
                        + updated.getEnrollmentId()
                    );
                }

                case "GET_ENROLLMENTS" -> {
                    long sId = root.path("studentId").asLong(-1);
                    if (sId < 0) {
                        return ResponseEntity.ok(
                            "Missing studentId to retrieve enrollments."
                        );
                    }
                    List<EnrollmentDto> enrolls = 
                        enrollmentService.retrieveEnrollments(sId);
                    if (enrolls.isEmpty()) {
                        return ResponseEntity.ok("No enrollments found for student " + sId);
                    }
                    StringBuilder sb = new StringBuilder(
                        "Enrollments for student " + sId + ":\n"
                    );
                    for (EnrollmentDto e : enrolls) {
                        sb.append("- Enrollment ID: ")
                          .append(e.getEnrollmentId())
                          .append(", Course ID: ")
                          .append(e.getCourseId())
                          .append(", Status: ")
                          .append(e.getStatus())
                          .append("\n");
                    }
                    return ResponseEntity.ok(sb.toString());
                }

                case "DEREGISTER_STUDENT" -> {
                    long sId = root.path("studentId").asLong(-1);
                    if (sId < 0) {
                        return ResponseEntity.ok(
                            "Missing studentId for deregistering student."
                        );
                    }
                    studentService.listStudents(); 
                    enrollmentService.deregisterStudent(sId);
                    return ResponseEntity.ok(
                        "Student " + sId + " and all their enrollments have been removed."
                    );
                }

                default -> {
                    return ResponseEntity.ok(
                        "Sorry, I couldn't understand your request. Please try again."
                    );
                }
            }
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.ok("Error: " + rnfe.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.ok("Unexpected error: " + ex.getMessage());
        }
    }
}