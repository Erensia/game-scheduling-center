package com.erensia.gamescheduling.game;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PATCH /games/{gameId} 요청 바디.
 * name 필드는 포함하지 않는다 - 이름 수정은 04-api-spec.md에서 MVP 범위 밖으로 결정됨.
 */
@Getter
@RequiredArgsConstructor
public class GameUpdateRequest {

	@NotNull
	@Min(0)
	@Max(6)
	private final Integer resetDay;

	@NotNull
	@Min(1)
	private final Integer partySize;

}
