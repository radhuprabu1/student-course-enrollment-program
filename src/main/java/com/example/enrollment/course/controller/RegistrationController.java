package com.example.enrollment.course.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.enrollment.course.dto.CourseDto;
import com.example.enrollment.course.dto.EnrollmentDto;
import com.example.enrollment.course.dto.StudentDto;
import com.example.enrollment.course.entity.enums.EnrollmentStatus;
import com.example.enrollment.course.service.course.CourseService;
import com.example.enrollment.course.service.enrollment.EnrollmentService;
import com.example.enrollment.course.service.student.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(
		name = "CRUD REST APIs for Course Registration Resource",
		description = "CRUD REST APIs - Add Student, List Available Courses, List Students, "
				+ "Enroll Student, Deregister Course, Swap Course, Deregister Student, "
				+ "List Enrollments"
		)
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RegistrationController {

    private final CourseService courseService;
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    // --- Courses ---

    @Operation(
    		summary = "Get Courses REST API",
    		description = "List all the available courses in the database"
    		)
    @ApiResponse(
    		responseCode = "200",
    		description = "HTTP Status 200 Successful"
    		)
    /** GET /api/courses */
    @GetMapping("/courses")
    public ResponseEntity<Collection<CourseDto>> listCourses() {
        return ResponseEntity.ok(courseService.searchCourses());
    }

    // --- Students ---
    @Operation(
    		summary = "Save Student REST API",
    		description = "Adds a student in the database for enrollment"
    		)
    @ApiResponse(
    		responseCode = "200",
    		description = "HTTP Status 200 Successful"
    		)
    /** POST /api/students */
    @PostMapping("/students")
    public ResponseEntity<StudentDto> addStudent(@Valid @RequestBody StudentDto student) {
        StudentDto created = studentService.addStudent(student);
        return ResponseEntity.ok(created);
    }

    @Operation(
    		summary = "Get Students REST API",
    		description = "List all the saved students from the database"
    		)
    @ApiResponse(
    		responseCode = "200",
    		description = "HTTP Status 200 Successful"
    		)
    /** GET /api/students */
    @GetMapping("/students")
    public ResponseEntity<Collection<StudentDto>> listStudents() {
        return ResponseEntity.ok(studentService.listStudents());
    }

    // --- Enrollments ---

    @Operation(
    		summary = "Post Enrollment REST API",
    		description = "Enroll a student to a course"
    		)
    @ApiResponse(
    		responseCode = "201",
    		description = "HTTP Status 201 Created"
    		)
    @ApiResponse(
    		responseCode = "202",
    		description = "HTTP Status 202 Accepted"
    		)
    /** 
     * POST /api/enroll/{courseId}/student/{studentId}  
     * Enroll or waitlist the student
     */
    @PostMapping("/enroll/{courseId}/student/{studentId}")
    public ResponseEntity<EnrollmentDto> enroll(
            @PathVariable Long courseId,
            @PathVariable Long studentId
            ) {
        EnrollmentDto e = enrollmentService.enrollStudent(courseId, studentId);
        return ResponseEntity.status(
                e.getStatus() == EnrollmentStatus.ENROLLED ? 201 : 202
            ).body(e);
    }

    @Operation(
    		summary = "Update Courses REST API",
    		description = "Deregister from an enrolled course for the student"
    		)
    @ApiResponse(
    		responseCode = "204",
    		description = "HTTP Status 204 No Content"
    		)
    /** PUT /api/deregister/{courseId}/student/{studentId} */
    @PutMapping("/deregister/{courseId}/student/{studentId}")
    public ResponseEntity<Void> deregisterCourse(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        enrollmentService.deregisterCourse(courseId, studentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
    		summary = "Delete Student REST API",
    		description = "Delete a student and their enrolled course(s) from the database"
    		)
    @ApiResponse(
    		responseCode = "204",
    		description = "HTTP Status 204 No Content"
    		)
    /** DELETE /api/students/{studentId} */
    @DeleteMapping("/students/{studentId}")
    public ResponseEntity<String> deregisterStudent(@PathVariable Long studentId) {
        enrollmentService.deregisterStudent(studentId);
        return new ResponseEntity<>("Student Successfully Removed from the System!",
        		HttpStatus.NO_CONTENT);
    }

    @Operation(
    		summary = "Update Enrolled Course REST API",
    		description = "Swap Enrolled Course for the student"
    		)
    @ApiResponse(
    		responseCode = "200",
    		description = "HTTP Status 200 Successful"
    		)
    /** PUT /api/swap/{studentId}?from=oldCourse&to=newCourse */
    @PatchMapping("/swap/{studentId}")
    public ResponseEntity<EnrollmentDto> swapCourse(
            @PathVariable Long studentId,
            @RequestParam("from") Long oldCourseId,
            @RequestParam("to") Long newCourseId
            ) {
        EnrollmentDto updated = enrollmentService
            .updateCourseEnrollment(studentId, oldCourseId, newCourseId);
        return ResponseEntity.ok(updated);
    }

    @Operation(
    		summary = "Get Enrollments REST API",
    		description = "List all the enrollments for the student"
    		)
    @ApiResponse(
    		responseCode = "200",
    		description = "HTTP Status 200 Successful"
    		)
    /** GET /api/enrollments/student/{studentId} */
    @GetMapping("/enrollments/student/{studentId}")
    public ResponseEntity<List<EnrollmentDto>> getEnrollments(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
            enrollmentService.retrieveEnrollments(studentId));
    }
}
