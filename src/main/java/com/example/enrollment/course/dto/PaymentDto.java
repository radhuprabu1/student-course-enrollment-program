package com.example.enrollment.course.dto;

import com.example.enrollment.course.entity.enums.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents Payment Entity in the database
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PaymentDto {

    @NotNull
	@Schema(description = "Payment Id")
    private Long paymentId;

    @NotNull
    private Long enrollmentId;

    @NotBlank
    private String paymentMethod;

    @NotNull
    private PaymentStatus paymentStatus;
}