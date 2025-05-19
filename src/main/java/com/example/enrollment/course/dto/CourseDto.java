package com.example.enrollment.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents Course Entity in the database
 */
@Schema(
		description = "CourseDto Model Information"
		)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CourseDto {

	@Schema(description = "Course Id")
    private Long courseId;
	
	@Schema(description = "Course Name")
    private String courseName;
	
	@Schema(description = "Course Duration")
    private String duration;
	
	@Schema(description = "Course Fees")
    private double fees;
	
	@Schema(description = "Available Seats for the Course")
    private Long availableSeats;

}

