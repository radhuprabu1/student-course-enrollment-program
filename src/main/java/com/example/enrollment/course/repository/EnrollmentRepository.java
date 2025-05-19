package com.example.enrollment.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.enrollment.course.entity.Enrollment;
import com.example.enrollment.course.entity.enums.EnrollmentStatus;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	/** Find all enrollments of a student */
	List<Enrollment> findByStudentId(Long studentId);

	/** Find one enrollment by student & course */
	Optional<Enrollment> 
	findByStudentIdAndCourseId(Long studentId, Long courseId);

	/** Find all enrollments for a course (used for cleanup or reporting) */
	List<Enrollment> findByCourseId(Long courseId);

	/** Get all waitlisted enrollments for a course (status = WAITLISTED) */
	List<Enrollment> findByCourseIdAndStatusOrderByWaitlistPositionAsc(Long courseId, EnrollmentStatus status);
}
