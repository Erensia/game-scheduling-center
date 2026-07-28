package com.erensia.gamescheduling.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리기. 04-api-spec.md 에러 코드 체계를 따라
 * 컨트롤러/서비스에서 던진 예외를 {code, message, path, timestamp} 형태의 응답(ErrorResponse)으로 변환한다.
 *
 * 지금은 아는 케이스 두 개만 처리한다:
 * 1) ResourceNotFoundException -> 404
 * 2) @Valid 검증 실패(MethodArgumentNotValidException) -> 400, code="VALIDATION_ERROR"
 *
 * 새 도메인(Character, WeeklyContent, Template, Party) 작업하다가
 * INVALID_SLOT_INDEX 같은 새 예외가 필요해지면, 그때 핸들러 메서드를 하나씩 추가한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(
			ResourceNotFoundException ex, HttpServletRequest request) {
		String code = ex.getCode();
		String message = ex.getMessage();
		String path = request.getRequestURI();
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(code, message, path));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(
			MethodArgumentNotValidException ex, HttpServletRequest request) {
		String code = "VALIDATION_ERROR";
		String path = request.getRequestURI();
		String message = "";
		
		for(FieldError error : ex.getBindingResult().getFieldErrors()) {
			message += error.getField() + " " + error.getDefaultMessage() + " ";  
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(code, message, path));		
	}

}
