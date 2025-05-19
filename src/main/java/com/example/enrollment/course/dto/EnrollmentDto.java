package com.example.enrollment.course.dto;

import com.example.enrollment.course.entity.enums.EnrollmentStatus;
import com.example.enrollment.course.entity.enums.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents Enrollment Entity in the database
 */
@Schema(
		description = "EnrollmentDto Model Information"
		)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EnrollmentDto {
    /** Unique ID for this enrollment record */
    @NotNull
	@Schema(description = "Enrollment Id")
    private Long enrollmentId;

    /** FK to the Course */
    @NotNull
	@Schema(description = "Student Id")
    private Long studentId;

    /** FK to the Student */
    @NotNull
	@Schema(description = "Course Id")
    private Long courseId;

    /** ENROLLED if a seat was available; WAITLISTED otherwise */
    @NotNull
	@Schema(description = "Enrollment Status")
    private EnrollmentStatus status;

    /**
     * Position in the waitlist queue; null if status==ENROLLED.
     * Lower values get promoted first.
     */
	@Schema(description = "Waitlist Number (if any)")
    private Long waitlistPosition;

    /** PENDING until payment is done, then COMPLETED */
	@Schema(description = "Payment Status")
    private PaymentStatus paymentStatus;
    

    
    
}
