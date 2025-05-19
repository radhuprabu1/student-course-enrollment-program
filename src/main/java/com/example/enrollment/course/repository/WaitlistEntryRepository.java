package com.example.enrollment.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.enrollment.course.entity.WaitlistEntry;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistEntryRepository extends JpaRepository<WaitlistEntry, Long> {

	/** All waitlist entries for a course, ordered oldest -> newest */
	List<WaitlistEntry> 
	findByCourseIdOrderByTimestampAsc(Long courseId);

	/** First (next) entry in the waitlist for a course */
	Optional<WaitlistEntry> 
	findFirstByCourseIdOrderByTimestampAsc(Long courseId);

	/** Count how many are waiting */
	Long countByCourseId(Long courseId);

	/** Remove entry by enrollment FK */
	void deleteByEnrollmentId(Long enrollmentId);
}
