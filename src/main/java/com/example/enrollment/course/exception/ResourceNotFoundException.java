package com.example.enrollment.course.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
	private static final long serialVersionUID = 904802229309382175L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

}
