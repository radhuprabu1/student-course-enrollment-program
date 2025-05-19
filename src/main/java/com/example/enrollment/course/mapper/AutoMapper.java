package com.example.enrollment.course.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.enrollment.course.dto.CourseDto;
import com.example.enrollment.course.dto.EnrollmentDto;
import com.example.enrollment.course.dto.PaymentDto;
import com.example.enrollment.course.dto.StudentDto;
import com.example.enrollment.course.entity.Course;
import com.example.enrollment.course.entity.Enrollment;
import com.example.enrollment.course.entity.Payment;
import com.example.enrollment.course.entity.Student;

@Mapper
public interface AutoMapper {
	
	AutoMapper MAPPER = Mappers.getMapper(AutoMapper.class);
	//Convert Dto -> Models
	CourseDto mapToCourseDto(Course course);
	PaymentDto mapToPaymentDto(Payment payment);
	EnrollmentDto mapToEnrollmentDto(Enrollment enrollment);
	StudentDto mapToStudentDto(Student student);
	//Convert Models -> Dto
	Course mapToCourse(CourseDto courseDto);
	Payment mapToPayment(PaymentDto paymentDto);
	Enrollment enrollment(EnrollmentDto enrollmentDto);
	Student mapToStudent(StudentDto studentDto);
}
