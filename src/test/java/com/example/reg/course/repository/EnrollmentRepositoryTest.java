package com.example.reg.course.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.enrollment.course.entity.Enrollment;
import com.example.enrollment.course.entity.enums.EnrollmentStatus;
import com.example.enrollment.course.entity.enums.PaymentStatus;
import com.example.enrollment.course.repository.EnrollmentRepository;

@DataJpaTest
class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository repo;

    @Test
    @DisplayName("findByStudentId and findByCourseId combine correctly")
    void findByStudentAndCourse_andStatus() {
        // given 3 enrollments: 2 for student 1, course 10; 1 for student 2, course 20
        Enrollment e1 = new Enrollment(null, 1L, 10L, EnrollmentStatus.ENROLLED, null, PaymentStatus.PENDING);
        Enrollment e2 = new Enrollment(null, 1L, 20L, EnrollmentStatus.WAITLISTED, 1L, PaymentStatus.PENDING);
        Enrollment e3 = new Enrollment(null, 2L, 20L, EnrollmentStatus.ENROLLED, null, PaymentStatus.PENDING);
        repo.saveAll(List.of(e1, e2, e3));

        // when findByStudentId(1)
        List<Enrollment> byStudent = repo.findByStudentId(1L);
        assertThat(byStudent).hasSize(2);

        // when findByCourseId(10)
        List<Enrollment> byCourse = repo.findByCourseId(20L);
        assertThat(byCourse).hasSize(2);

        // when findByStudentIdAndCourseId
        Optional<Enrollment> single = repo.findByStudentIdAndCourseId(2L, 20L);
        assertThat(single).isPresent()
                          .get().extracting(Enrollment::getStudentId, Enrollment::getCourseId)
                          .containsExactly(2L, 20L);

        // when findByCourseIdAndStatusOrderByWaitlistPositionAsc
        List<Enrollment> waitlisted = repo.findByCourseIdAndStatusOrderByWaitlistPositionAsc(
            20L, EnrollmentStatus.WAITLISTED);
        assertThat(waitlisted).hasSize(1)
                               .first()
                               .extracting(Enrollment::getStatus)
                               .isEqualTo(EnrollmentStatus.WAITLISTED);
    }
}
