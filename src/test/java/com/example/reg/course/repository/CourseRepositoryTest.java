package com.example.reg.course.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.enrollment.course.entity.Course;
import com.example.enrollment.course.repository.CourseRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourseRepositoryTest {

    @Autowired
    private CourseRepository repo;

    @Test
    @DisplayName("findByCourseId returns empty if not present")
    void findByCourseId_empty() {
        Optional<Course> found = repo.findByCourseId(999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("decrementSeat and incrementSeat behave correctly")
    void seatUpdates_andFindByCourseId() {
        // given: a course with 2 seats
        Course c = new Course();
        c.setCourseId(1L);
        c.setCourseName("Test");
        c.setDuration("1h");
        c.setFees(50.0);
        c.setAvailableSeats(2L);
        repo.save(c);

        // when: decrement twice
        int r1 = repo.decrementSeat(1L);
        int r2 = repo.decrementSeat(1L);

        // then: both succeed and seats go to 0
        assertThat(r1).isEqualTo(1);
        assertThat(r2).isEqualTo(1);
        Course updated = repo.findByCourseId(1L).orElseThrow();
        assertThat(updated.getAvailableSeats()).isEqualTo(2L);

        // when: decrement at zero
        int r3 = repo.decrementSeat(1L);
        assertThat(r3).isEqualTo(0);

        // and: increment
        int r4 = repo.incrementSeat(1L);
        assertThat(r4).isEqualTo(1);
        Course afterInc = repo.findByCourseId(1L).orElseThrow();
        assertThat(afterInc.getAvailableSeats()).isEqualTo(2L);
    }
}
