package com.erensia.gamescheduling.game;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * POST /games 요청 바디. 04-api-spec.md 검증 규칙:
 * 이름 공백 불가 / resetDay 0~6 / partySize 1 이상
 */
@Getter
@RequiredArgsConstructor
public class GameCreateRequest {

	@NotBlank
	private final String name;

	@NotNull
	@Min(0)
	@Max(6)
	private final Integer resetDay;

	@NotNull
	@Min(1)
	private final Integer partySize;

}
