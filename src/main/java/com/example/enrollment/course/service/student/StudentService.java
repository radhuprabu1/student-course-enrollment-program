package com.example.enrollment.course.service.student;

import java.util.Collection;

import com.example.enrollment.course.dto.StudentDto;

import jakarta.validation.Valid;

public interface StudentService {
    /** Add a new student to the system */
    StudentDto addStudent(@Valid StudentDto student);
    /** List all students */
    Collection<StudentDto> listStudents();
}
