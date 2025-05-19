package com.example.enrollment.course.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
			ResourceNotFoundException e,
			WebRequest wr
			) {
		ErrorDetails errorDetails = new ErrorDetails(
				LocalDateTime.now(),
				e.getMessage(),
				wr.getDescription(false),
				"REQUEST NOT FOUND"
				);
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ErrorDetails> handleResourceAlreadyExists(
			ResourceAlreadyExistsException e,
			WebRequest wr
			) {
		ErrorDetails errorDetails = new ErrorDetails(
				LocalDateTime.now(),
				e.getMessage(),
				wr.getDescription(false),
				"DUPLICATE RESOURCE FOUND"
				);
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> handleGlobalException(
			Exception e, WebRequest wr
			) {
		ErrorDetails errDetails = new ErrorDetails(
				LocalDateTime.now(),
				e.getMessage(),
				wr.getDescription(false),
				"INTERNAL SERVER ERROR"
				);
		return new ResponseEntity<>(errDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException e,
			HttpHeaders headers,
			HttpStatusCode status,WebRequest request
			) {
		Map<String, String> errors = new HashMap<>();
		List<ObjectError> errorList = e.getBindingResult().getAllErrors();

		errorList.forEach(error ->{
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.put(fieldName, message);
		});

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

}
