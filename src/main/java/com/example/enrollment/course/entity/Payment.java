package com.example.enrollment.course.entity;

import com.example.enrollment.course.entity.enums.PaymentStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payment details for an enrollment.
 * Not implemented yet; Status -> Pending by default.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Payment {
	

    @NotNull
    private Long paymentId;

    @NotNull
    private Long enrollmentId;

    @NotBlank
    private String paymentMethod;

    @NotNull
    private PaymentStatus paymentStatus;
    
}