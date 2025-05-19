package com.example.enrollment.course.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.enrollment.course.entity.Course;

import jakarta.transaction.Transactional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /** Fetch a course or empty if not found */
    Optional<Course> findByCourseId(Long courseId);

    /** Decrease available seats by 1 if seats > 0, returns # of rows updated */
    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.availableSeats = c.availableSeats - 1 "
         + "WHERE c.courseId = :courseId AND c.availableSeats > 0")
    int decrementSeat(@Param("courseId") Long courseId);

    /** Increase available seats by 1 */
    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.availableSeats = c.availableSeats + 1 "
         + "WHERE c.courseId = :courseId")
    int incrementSeat(@Param("courseId") Long courseId);
}
