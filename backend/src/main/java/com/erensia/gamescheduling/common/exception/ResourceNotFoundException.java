package com.erensia.gamescheduling.common.exception;

import lombok.Getter;

/**
 * 요청한 리소스를 찾을 수 없을 때 던지는 공통 예외.
 * 04-api-spec.md의 에러 코드 체계 - 404 (GAME_NOT_FOUND, CHARACTER_NOT_FOUND 등)에 대응한다.
 *
 * 나중에 GlobalExceptionHandler(@RestControllerAdvice)에서 이 예외를 잡아
 * HTTP 404 응답 + {code, message, path, timestamp} 형태로 변환할 예정이다.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

	/** 예: "GAME_NOT_FOUND", "CHARACTER_NOT_FOUND" - 04-api-spec.md 에러 코드 표 참고 */
	private final String code;

	public ResourceNotFoundException(String code, String message) {
		super(message);
		this.code = code;
	}

}
