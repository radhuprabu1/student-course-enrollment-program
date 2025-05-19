package com.example.reg.course.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.enrollment.course.dto.CourseDto;
import com.example.enrollment.course.entity.Course;
import com.example.enrollment.course.repository.CourseRepository;
import com.example.enrollment.course.service.course.CourseServiceImpl;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository repo;
    
    @InjectMocks
    private CourseServiceImpl service;

    @Test
    void searchCourses_returnsEmpty_whenNoCourses() {
        when(repo.findAll()).thenReturn(List.of());
        List<CourseDto> list = service.searchCourses();
        assertTrue(list.isEmpty());
        verify(repo).findAll();
    }

    @Test
    void searchCourses_returnsDtos_forExistingCourses() {
        Course c1 = new Course(101L, "Java", "10h", 100.0, 5L);
        Course c2 = new Course(102L, "Spring", "20h", 200.0, 0L);
        when(repo.findAll()).thenReturn(List.of(c1, c2));

        List<CourseDto> dtos = service.searchCourses();
        assertEquals(2, dtos.size());
        assertEquals(101L, dtos.get(0).getCourseId());
        assertEquals("Spring", dtos.get(1).getCourseName());
        verify(repo).findAll();
    }
}
