package com.example.enrollment.course.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A record that a given Enrollment is waitlisted for the Course.
 * Links by FK to both Enrollment and Course.
 */
@Entity
@Table(name = "waitlist_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor 
@EqualsAndHashCode(of = "entryId")
@ToString
public class WaitlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long entryId;

    /** FK to the course */
    @NotNull
    @Column(name = "course_id", nullable = false, updatable = false)
    private Long courseId;

    /** FK to the enrollment */
    @NotNull
    @Column(name = "enrollment_id", nullable = false, unique = true, updatable = false)
    private Long enrollmentId;

    /** When they joined the waitlist */
    @NotNull
    @Column(nullable = false, updatable = false)
    private Instant timestamp;
}