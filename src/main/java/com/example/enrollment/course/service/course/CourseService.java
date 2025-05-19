package com.example.enrollment.course.service.course;

import java.util.Collection;

import com.example.enrollment.course.dto.CourseDto;

public interface CourseService {

	/** List all available courses */
    Collection<CourseDto> searchCourses();

}
