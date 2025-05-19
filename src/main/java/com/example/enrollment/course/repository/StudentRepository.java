package com.example.enrollment.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.enrollment.course.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

}
