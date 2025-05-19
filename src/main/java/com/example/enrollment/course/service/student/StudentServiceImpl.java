package com.example.enrollment.course.service.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.enrollment.course.dto.StudentDto;
import com.example.enrollment.course.entity.Student;
import com.example.enrollment.course.exception.ResourceAlreadyExistsException;
import com.example.enrollment.course.mapper.AutoMapper;
import com.example.enrollment.course.repository.StudentRepository;

import java.util.List;

/**
 * Implement Registration Controller functions w.r.t Student Operations
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

	private final StudentRepository studentRepository;

	/**
	 * Adds a new student. If the DTO carries an ID that already exists,
	 * throws ResourceAlreadyExistsException.
	 */
	@Override
	public StudentDto addStudent(StudentDto studentDto) {
		Long dtoId = studentDto.getStudentId();
		if (dtoId != null && studentRepository.existsById(dtoId)) {
			throw new ResourceAlreadyExistsException(
					"Student Already Exists with id: " + dtoId);
		}

		Student toSave = AutoMapper.MAPPER.mapToStudent(studentDto);
		Student saved  = studentRepository.save(toSave);

		return AutoMapper.MAPPER.mapToStudentDto(saved);
	}

	/**
	 * Returns all students as DTOs.
	 * If you ever need pagination, swap to Pageable + Page<StudentDto>.
	 */
	@Override
	public List<StudentDto> listStudents() {
		return studentRepository.findAll().stream()
				.map(AutoMapper.MAPPER::mapToStudentDto)
				.collect(java.util.stream.Collectors.toList());
	}
}
