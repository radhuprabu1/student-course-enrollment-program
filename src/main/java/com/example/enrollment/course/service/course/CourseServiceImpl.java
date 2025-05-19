package com.example.enrollment.course.service.course;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enrollment.course.dto.CourseDto;
import com.example.enrollment.course.mapper.AutoMapper;
import com.example.enrollment.course.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implements Registration Controller functions w.r.t Course operations
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    /**
     * Lists all courses as DTOs.
     */
    @Override
    public List<CourseDto> searchCourses() {
        return courseRepository.findAll().stream()
                .map(AutoMapper.MAPPER::mapToCourseDto)
                .toList();
    }
}
