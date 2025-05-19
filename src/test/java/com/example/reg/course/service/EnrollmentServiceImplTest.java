package com.example.reg.course.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.enrollment.course.dto.EnrollmentDto;
import com.example.enrollment.course.entity.Enrollment;
import com.example.enrollment.course.entity.WaitlistEntry;
import com.example.enrollment.course.entity.enums.EnrollmentStatus;
import com.example.enrollment.course.entity.enums.PaymentStatus;
import com.example.enrollment.course.exception.ResourceAlreadyExistsException;
import com.example.enrollment.course.exception.ResourceNotFoundException;
import com.example.enrollment.course.repository.CourseRepository;
import com.example.enrollment.course.repository.EnrollmentRepository;
import com.example.enrollment.course.repository.StudentRepository;
import com.example.enrollment.course.repository.WaitlistEntryRepository;
import com.example.enrollment.course.service.enrollment.EnrollmentServiceImpl;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    EnrollmentRepository enrollmentRepo;
    
    @Mock
    CourseRepository courseRepo;
    
    @Mock
    StudentRepository studentRepo;
    
    @Mock
    WaitlistEntryRepository waitlistRepo;
    
    @InjectMocks
    EnrollmentServiceImpl service;

    @Test
    void enrollStudent_enrolledWhenSeatAvailable() {
        long courseId = 1L, studentId = 2L;
        when(studentRepo.existsById(studentId)).thenReturn(true);
        when(courseRepo.existsById(courseId)).thenReturn(true);
        when(enrollmentRepo.findByStudentIdAndCourseId(studentId, courseId))
            .thenReturn(Optional.empty());
        when(courseRepo.decrementSeat(courseId)).thenReturn(1);

        // Simulate save assigns ID
        ArgumentCaptor<Enrollment> cap = ArgumentCaptor.forClass(Enrollment.class);
        when(enrollmentRepo.save(cap.capture()))
            .thenAnswer(inv -> {
                Enrollment e = inv.getArgument(0);
                e.setEnrollmentId(99L);
                return e;
            });

        EnrollmentDto dto = service.enrollStudent(courseId, studentId);
        assertEquals(99L, dto.getEnrollmentId());
        assertEquals(EnrollmentStatus.ENROLLED, dto.getStatus());
        assertNull(dto.getWaitlistPosition());

        Enrollment saved = cap.getValue();
        assertEquals(PaymentStatus.PENDING, saved.getPaymentStatus());
        verify(waitlistRepo, never()).save(any());
    }

    @Test
    void enrollStudent_waitlistedWhenNoSeat() {
        long c = 10L, s = 20L;
        when(studentRepo.existsById(s)).thenReturn(true);
        when(courseRepo.existsById(c)).thenReturn(true);
        when(enrollmentRepo.findByStudentIdAndCourseId(s, c))
            .thenReturn(Optional.empty());
        when(courseRepo.decrementSeat(c)).thenReturn(0);
        when(waitlistRepo.countByCourseId(c)).thenReturn(3L);
        when(enrollmentRepo.save(any()))
            .thenAnswer(inv -> {
                Enrollment e = inv.getArgument(0);
                e.setEnrollmentId(42L);
                return e;
            });

        EnrollmentDto dto = service.enrollStudent(c, s);
        assertEquals(42L, dto.getEnrollmentId());
        assertEquals(EnrollmentStatus.WAITLISTED, dto.getStatus());
        assertEquals(4L, dto.getWaitlistPosition());
        verify(waitlistRepo).save(any(WaitlistEntry.class));
    }

    @Test
    void enrollStudent_throws_whenStudentMissing() {
        when(studentRepo.existsById(5L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(
            ResourceNotFoundException.class,
            () -> service.enrollStudent(1L, 5L)
        );
        assertTrue(ex.getMessage().contains("Student Not Found"));
    }

    @Test
    void enrollStudent_throws_whenCourseMissing() {
        when(studentRepo.existsById(2L)).thenReturn(true);
        when(courseRepo.existsById(3L)).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(
            ResourceNotFoundException.class,
            () -> service.enrollStudent(3L, 2L)
        );
        assertTrue(ex.getMessage().contains("Course Not Found"));
    }

    @Test
    void enrollStudent_throws_whenDuplicateEnrollment() {
        when(studentRepo.existsById(4L)).thenReturn(true);
        when(courseRepo.existsById(6L)).thenReturn(true);
        when(enrollmentRepo.findByStudentIdAndCourseId(4L, 6L))
            .thenReturn(Optional.of(new Enrollment()));
        assertThrows(ResourceAlreadyExistsException.class,
            () -> service.enrollStudent(6L, 4L));
    }

    @Test
    void deregisterCourse_promotesNextWaitlisted() {
        long c = 7L, s = 8L, entryId = 100L, enrolId = 200L;
        Enrollment e = new Enrollment(enrolId, s, c, null, null, null);
        when(enrollmentRepo.findByStudentIdAndCourseId(s, c)).thenReturn(Optional.of(e));
        when(waitlistRepo.findFirstByCourseIdOrderByTimestampAsc(c))
            .thenReturn(Optional.of(new WaitlistEntry(entryId, c, enrolId, Instant.now())));
        when(courseRepo.incrementSeat(c)).thenReturn(1);
        when(courseRepo.decrementSeat(c)).thenReturn(1);
        when(enrollmentRepo.findById(enrolId))
            .thenReturn(Optional.of(e));

        service.deregisterCourse(c, s);

        verify(enrollmentRepo).deleteById(enrolId);
        verify(courseRepo).incrementSeat(c);
        verify(waitlistRepo).deleteById(entryId);
        verify(enrollmentRepo, atLeastOnce()).save(e);
    }

    @Test
    void deregisterCourse_throwsWhenMissingEnrollment() {
        when(enrollmentRepo.findByStudentIdAndCourseId(9L, 9L))
            .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
            () -> service.deregisterCourse(9L, 9L));
    }

    @Test
    void retrieveEnrollments_success() {
        long s = 3L;
        when(studentRepo.existsById(s)).thenReturn(true);
        Enrollment e1 = new Enrollment(1L, s, 10L, EnrollmentStatus.ENROLLED, null, PaymentStatus.PENDING);
        Enrollment e2 = new Enrollment(2L, s, 11L, EnrollmentStatus.WAITLISTED, 2L, PaymentStatus.PENDING);
        when(enrollmentRepo.findByStudentId(s)).thenReturn(List.of(e1, e2));

        List<EnrollmentDto> dtos = service.retrieveEnrollments(s);
        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getEnrollmentId());
    }

    @Test
    void retrieveEnrollments_throwsWhenStudentMissing() {
        when(studentRepo.existsById(12L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
            () -> service.retrieveEnrollments(12L));
    }
}
