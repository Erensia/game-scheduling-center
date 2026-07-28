package com.erensia.gamescheduling.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Game 응답 DTO. id/createdAt까지 내려줄지는 04-api-spec.md TBD 항목
 * ("각 엔드포인트의 요청/응답 JSON 스키마 상세")이라 지금 판단해서 정하면 됨.
 * createdAt까지는 내리지 않는 것으로 결정 - 클라이언트가 쓸 일이 없다고 판단.
 */
@Getter
@RequiredArgsConstructor
public class GameResponse {

	private final Long id;
	private final String name;
	private final Integer resetDay;
	private final Integer partySize;

	public static GameResponse from(Game game) {
		return new GameResponse(game.getId(), game.getName(), game.getResetDay(), game.getPartySize());
	}

}
