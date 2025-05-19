package com.example.enrollment.course.service.enrollment;

import java.util.List;

import com.example.enrollment.course.dto.EnrollmentDto;

/**
 * Core enrollment operations:
 *  - enroll or waitlist
 *  - deregister from a course
 *  - deregister student entirely
 *  - swap courses
 *  - list a student’s enrollments
 */
public interface EnrollmentService {
    EnrollmentDto enrollStudent(Long courseId, Long studentId);
    void deregisterCourse(Long courseId, Long studentId);
    void deregisterStudent(Long studentId);
    EnrollmentDto updateCourseEnrollment(Long studentId, Long oldCourseId, Long newCourseId);
    List<EnrollmentDto> retrieveEnrollments(Long studentId);
}
