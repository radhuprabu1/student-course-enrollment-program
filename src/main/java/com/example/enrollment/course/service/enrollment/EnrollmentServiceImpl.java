package com.example.enrollment.course.service.enrollment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.enrollment.course.dto.EnrollmentDto;
import com.example.enrollment.course.entity.Enrollment;
import com.example.enrollment.course.exception.ResourceNotFoundException;
import com.example.enrollment.course.mapper.AutoMapper;
import com.example.enrollment.course.repository.CourseRepository;
import com.example.enrollment.course.repository.EnrollmentRepository;
import com.example.enrollment.course.repository.StudentRepository;
import com.example.enrollment.course.repository.WaitlistEntryRepository;
import com.example.enrollment.course.service.util.EnrollmentUtil;

import lombok.RequiredArgsConstructor;

/**
 * Implements Registration Controller operations w.r.t to Enrollments
 */
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

	private final EnrollmentRepository enrollmentRepo;
	private final CourseRepository courseRepo;
	private final StudentRepository studentRepo;
	private final WaitlistEntryRepository waitlistRepo;

	/**
	 * Enroll Student to the given course by course_id
	 * @param courseId
	 * @param studentId
	 * @return EnrollmentDto to the user
	 */
	@Override
	@Transactional
	public EnrollmentDto enrollStudent(Long courseId, Long studentId) {
		return new EnrollmentUtil(
				enrollmentRepo, courseRepo, studentRepo, waitlistRepo
				).enrollStudent(courseId, studentId);
	}

	/**
	 * Deregister given course from the student
	 * @param courseId - Course data can be extracted using courseId
	 * @param studentId - Student record can be fetched using studentId
	 */
	@Override
	@Transactional
	public void deregisterCourse(Long courseId, Long studentId) {
		new EnrollmentUtil(
				enrollmentRepo, courseRepo, studentRepo, waitlistRepo
				).deRegisterCourse(courseId, studentId);
	}

	/**
	 * Delete Student Record from the system
	 * @param studentId - Student record can be fetched using studentId
	 */
	@Override
	@Transactional
	public void deregisterStudent(Long studentId) {
		// Validate Student Existence
		if (!studentRepo.existsById(studentId)) {
			throw new ResourceNotFoundException("Student Not Found with id: " + studentId);
		}
		// Extract all the enrollments of the student
		List<Enrollment> list = enrollmentRepo.findByStudentId(studentId);
		// Deregister each enrollment associated with the student
		list.forEach(e -> new EnrollmentUtil(
				enrollmentRepo, courseRepo, studentRepo, waitlistRepo
				).deRegisterCourse(e.getCourseId(), studentId));

		// Delete student from the system
		studentRepo.deleteById(studentId);
	}

	/**
	 * Swap Enrolled Course of the student
	 * @param studentId - Fetch Student Record from the system
	 * @param oldCourseId - Course to be swapped
	 * @param newCourseId - New Course to be enrolled
	 * @return Updated EnrollmentDto to the user
	 */
	@Override
	@Transactional
	public EnrollmentDto updateCourseEnrollment(
			Long studentId, Long oldCourseId, Long newCourseId) {
		// Validate Old & New Courses Existence.
		if (!courseRepo.existsById(oldCourseId)) {
			throw new ResourceNotFoundException("Course Not Found with id: " + oldCourseId);
		}
		if (!courseRepo.existsById(newCourseId)) {
			throw new ResourceNotFoundException("Course Not Found with id: " + newCourseId);
		}
		// De-register old course
		new EnrollmentUtil(
				enrollmentRepo, courseRepo, studentRepo, waitlistRepo
				).deRegisterCourse(oldCourseId, studentId);
		// Enroll new course
		return new EnrollmentUtil(
				enrollmentRepo, courseRepo, studentRepo, waitlistRepo
				).enrollStudent(newCourseId, studentId);
	}

	/**
	 * Retrieve all the enrollments of the student
	 * @param studentId - Fetches Student Record
	 * @return List of EnrollmentDto to the user
	 */
	@Override
	public List<EnrollmentDto> retrieveEnrollments(Long studentId) {
		// Ensure Student Exists
		if (!studentRepo.existsById(studentId)) {
			throw new ResourceNotFoundException("Student Not Found with id: " + studentId);
		}

		return enrollmentRepo.findByStudentId(studentId).stream()
				.map(AutoMapper.MAPPER::mapToEnrollmentDto)
				.toList();
	}
}
