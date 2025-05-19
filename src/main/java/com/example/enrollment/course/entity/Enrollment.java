package com.example.enrollment.course.entity;

import com.example.enrollment.course.entity.enums.EnrollmentStatus;
import com.example.enrollment.course.entity.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a student’s enrollment in one course.
 * Holds foreign keys to Student and Course.
 */
@Entity
@Table(
  name = "enrollments",
  uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor 
@EqualsAndHashCode(of = "enrollmentId")
@ToString
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    /** FK to the Student table */
    @NotNull
    @Column(name = "student_id", nullable = false, updatable = false)
    private Long studentId;

    /** FK to the Course table */
    @NotNull
    @Column(name = "course_id", nullable = false, updatable = false)
    private Long courseId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    /** Null when ENROLLED; stores queue position otherwise */
    @Column(name = "waitlist_number")
    private Long waitlistPosition;

    /** Not yet implemented; PENDING by default */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;
}