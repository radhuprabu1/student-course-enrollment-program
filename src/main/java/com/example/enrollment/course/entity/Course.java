package com.example.enrollment.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A course offered by the institution
 */
@Entity
@Table(name = "courses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor 
@EqualsAndHashCode(of = "courseId")
@ToString
public class Course {

    @Id
    @Column(name = "course_id")
    private Long courseId;  

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(nullable = false)
    private String duration;

    @Min(0)
    @Column(nullable = false)
    private double fees;

    @Min(0)
    @Column(name = "available_seats", nullable = false)
    private Long availableSeats;
}