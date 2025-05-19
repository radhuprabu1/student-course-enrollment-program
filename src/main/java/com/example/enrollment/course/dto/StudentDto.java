package com.example.enrollment.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents Student Entity in the database
 */
@Schema(
		description = "StudentDto Model Information"
		)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class StudentDto {
	
	@Schema(description = "Student Id")
	private Long studentId;
	
	@NotBlank
	@Schema(description = "Student First Name")
	private String firstName;
	
	@Schema(description = "Student Last Name")
	private String lastName;
	
	@NotEmpty(message = "Student contact number shouldn't be null or empty")
	@Schema(description = "Student Contact Number")
	private String contactNumber;

}