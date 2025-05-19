package com.example.enrollment.course.service.util;

import java.time.Instant;
import java.util.List;

import com.example.enrollment.course.dto.EnrollmentDto;
import com.example.enrollment.course.entity.Enrollment;
import com.example.enrollment.course.entity.WaitlistEntry;
import com.example.enrollment.course.entity.enums.EnrollmentStatus;
import com.example.enrollment.course.entity.enums.PaymentStatus;
import com.example.enrollment.course.exception.ResourceAlreadyExistsException;
import com.example.enrollment.course.exception.ResourceNotFoundException;
import com.example.enrollment.course.mapper.AutoMapper;
import com.example.enrollment.course.repository.CourseRepository;
import com.example.enrollment.course.repository.EnrollmentRepository;
import com.example.enrollment.course.repository.StudentRepository;
import com.example.enrollment.course.repository.WaitlistEntryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Utility class for Enrollment Service
 */
@RequiredArgsConstructor
public class EnrollmentUtil {
	
	private final EnrollmentRepository enrollmentRepo;
	private final CourseRepository courseRepo;
	private final StudentRepository studentRepo;
	private final WaitlistEntryRepository waitlistRepo;

	/**
	 * Enrolls student -> course
	 * @param courseId
	 * @param studentId
	 * @return EnrollmentDto to the user
	 */
	@Transactional
	public EnrollmentDto enrollStudent(Long courseId, Long studentId) {
		// Validate student, course, enrollment
		ensureStudentExists(studentId);
		ensureCourseExists(courseId);
		ensureNotAlreadyEnrolled(studentId, courseId);

		// Initiate Enrollment record
		Enrollment enrollment = new Enrollment();
		enrollment.setStudentId(studentId);
		enrollment.setCourseId(courseId);
		enrollment.setPaymentStatus(PaymentStatus.PENDING);

		// Try seat decrement
		int rows = courseRepo.decrementSeat(courseId);
		if (rows > 0) {
			// seat successfully taken
			enrollment.setStatus(EnrollmentStatus.ENROLLED);
			enrollment.setWaitlistPosition(null);
		} else {
			// no seat available -> waitlist
			enrollment.setStatus(EnrollmentStatus.WAITLISTED);
			long pos = waitlistRepo.countByCourseId(courseId) + 1;
			enrollment.setWaitlistPosition(pos);
		}

		// Save/Create enrollment
		Enrollment saved = enrollmentRepo.save(enrollment);

		// If waitlisted, enqueue
		if (saved.getStatus() == EnrollmentStatus.WAITLISTED) {
			WaitlistEntry entry = new WaitlistEntry();
			entry.setCourseId(courseId);
			entry.setEnrollmentId(saved.getEnrollmentId());
			entry.setTimestamp(Instant.now());
			waitlistRepo.save(entry);
		}

		// Convert Enrollment Entity and Return EnrollmentDto
		return AutoMapper.MAPPER.mapToEnrollmentDto(saved);
	}

	/**
	 * Deregisters a course mapped to a student
	 * @param courseId - Fetch course object using courseId
	 * @param studentId - Fetch student object using studentId
	 */
	@Transactional
	public void deRegisterCourse(Long courseId, Long studentId) {
		// Validate Enrollment Existence
		Enrollment e = enrollmentRepo
				.findByStudentIdAndCourseId(studentId, courseId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Enrollment for student " + studentId + " in course " + courseId));
		// Delete Enrollment by Id from Enrollment Repository
		enrollmentRepo.deleteById(e.getEnrollmentId());

		// Free up seat -> Increase seat availability for the given course
		courseRepo.incrementSeat(courseId);

		// Promote next waitlisted
		waitlistRepo.findFirstByCourseIdOrderByTimestampAsc(courseId)
		.ifPresent(this::promoteWaitlist);
	}
	
		private void ensureStudentExists(Long studentId) {
		if (!studentRepo.existsById(studentId)) {
			throw new ResourceNotFoundException("Student Not Found with id: " + studentId);
		}
	}

	private void ensureCourseExists(Long courseId) {
		if (!courseRepo.existsById(courseId)) {
			throw new ResourceNotFoundException("Course Not Found with id: " + courseId);
		}
	}

	private void ensureNotAlreadyEnrolled(Long studentId, Long courseId) {
		if (enrollmentRepo.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
			throw new ResourceAlreadyExistsException(
					"Enrollment Already Exists with studentId: " + studentId + ", courseId: " + courseId);
		}
	}
	
	/**
	 * Process waitlist and promote the next position in the waitlist entry
	 * @param waitlistEntry 
	 */
	private void promoteWaitlist(WaitlistEntry entry) {
		// Remove from waitlist
		waitlistRepo.deleteById(entry.getEntryId());

		// Occupy freed seat -> Decrease seat availability for the given course 
		courseRepo.decrementSeat(entry.getCourseId());

		// Validate & Update the enrollment record
		Enrollment enrl = enrollmentRepo.findById(entry.getEnrollmentId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Enrollment Not Exists with id: " + entry.getEnrollmentId()));
		enrl.setStatus(EnrollmentStatus.ENROLLED);
		enrl.setWaitlistPosition(null);
		enrl.setPaymentStatus(PaymentStatus.PENDING);
		enrollmentRepo.save(enrl);

		// Re-compute positions for remaining waitlist
		List<WaitlistEntry> remaining = 
				waitlistRepo.findByCourseIdOrderByTimestampAsc(entry.getCourseId());
		long position = 1;
		for (WaitlistEntry we : remaining) {
			Enrollment wEn = enrollmentRepo.findById(we.getEnrollmentId())
					.orElseThrow();
			wEn.setWaitlistPosition(position++);
			enrollmentRepo.save(wEn);
		}
	}

}
