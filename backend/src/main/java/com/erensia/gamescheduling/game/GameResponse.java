package com.erensia.gamescheduling.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Game 응답 DTO. id/createdAt까지 내려줄지는 04-api-spec.md TBD 항목
 * ("각 엔드포인트의 요청/응답 JSON 스키마 상세")이라 지금 판단해서 정하면 됨.
 * createdAt까지는 내리지 않는 것으로 결정 - 클라이언트가 쓸 일이 없다고 판단.
 *
 * 설계 결정 - 이 DTO는 계속 불변(final)으로 유지한다:
 * GameCreateRequest/GameUpdateRequest와 달리 이 객체는 Jackson이 역직렬화(JSON→객체)할
 * 대상이 아니다. 서버 코드가 from(game)에서 직접 생성자를 호출해 만들고, Jackson은
 * 직렬화(객체→JSON, getter만 읽음)만 담당한다. 그래서 기본 생성자나 setter가 없어도
 * 문제가 생기지 않고, 불변을 유지해도 안전하다.
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
