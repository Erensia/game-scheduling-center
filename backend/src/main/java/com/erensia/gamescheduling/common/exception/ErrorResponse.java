package com.erensia.gamescheduling.common.exception;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * API 에러 응답 바디. 04-api-spec.md 에러 코드 체계: {code, message, path, timestamp}
 *
 * GlobalExceptionHandler가 각종 예외를 이 형태로 변환해서 클라이언트에 내려준다.
 */
@Getter
public class ErrorResponse {

	private final String code;
	private final String message;
	private final String path;
	private final LocalDateTime timestamp;

	public ErrorResponse(String code, String message, String path) {
		this.code = code;
		this.message = message;
		this.path = path;
		this.timestamp = LocalDateTime.now();
	}

}
