package com.example.reg.course.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.enrollment.course.dto.StudentDto;
import com.example.enrollment.course.entity.Student;
import com.example.enrollment.course.exception.ResourceAlreadyExistsException;
import com.example.enrollment.course.repository.StudentRepository;
import com.example.enrollment.course.service.student.StudentServiceImpl;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock 
    private StudentRepository repo;
    @InjectMocks
    private StudentServiceImpl service;

    @Test
    void addStudent_success_whenIdIsNull() {
        StudentDto dto = new StudentDto(null, "Alice", "Wonder", "123");
        Student savedEntity = new Student(1L, "Alice", "Wonder", "123");
        when(repo.save(any(Student.class))).thenReturn(savedEntity);

        StudentDto result = service.addStudent(dto);

        assertEquals(1L, result.getStudentId());
        assertEquals("Alice", result.getFirstName());
        verify(repo).save(any(Student.class));
    }

    @Test
    void addStudent_success_whenIdNotExists() {
        StudentDto dto = new StudentDto(5L, "Bob", "Builder", "456");
        when(repo.existsById(5L)).thenReturn(false);
        Student saved = new Student(5L, "Bob", "Builder", "456");
        when(repo.save(any())).thenReturn(saved);

        StudentDto result = service.addStudent(dto);

        assertEquals(5L, result.getStudentId());
        verify(repo).existsById(5L);
        verify(repo).save(any());
    }

    @Test
    void addStudent_throws_whenDuplicateId() {
        StudentDto dto = new StudentDto(2L, "Eve", "Dup", "789");
        when(repo.existsById(2L)).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(
            ResourceAlreadyExistsException.class,
            () -> service.addStudent(dto)
        );
        assertTrue(ex.getMessage().contains("Student Already Exists"));
        verify(repo, never()).save(any());
    }

    @Test
    void listStudents_returnsAll() {
        Student s1 = new Student(1L, "X", "Y", "111");
        Student s2 = new Student(2L, "A", "B", "222");
        when(repo.findAll()).thenReturn(List.of(s1, s2));

        List<StudentDto> dtos = service.listStudents();
        assertEquals(2, dtos.size());
        assertEquals("X", dtos.get(0).getFirstName());
        assertEquals("A", dtos.get(1).getFirstName());
        verify(repo).findAll();
    }
}
