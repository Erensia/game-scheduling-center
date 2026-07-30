package com.erensia.gamescheduling.character;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Character 응답 DTO.
 * GameResponse와 동일한 이유로 불변(final)을 유지한다 - 서버 코드가 from(character)에서
 * 직접 생성하고, Jackson은 직렬화(getter만 읽음)만 담당하기 때문.
 */
@Getter
@RequiredArgsConstructor
public class CharacterResponse {

	private final Long id;
	private final Long gameId;
	private final String name;
	private final boolean completed;

	// TODO: 정적 팩토리 메서드 작성 (GameResponse.from(game) 패턴 참고)
	//  - public static CharacterResponse from(Character character) { ... }
	//  - return new CharacterResponse(character.getId(), character.getGame().getId(),
	//        character.getName(), character.isCompleted());
	//  - 주의: gameId는 character.getGame().getId()로 꺼내되, 응답에 Game 전체를
	//          중첩시키지 않는다 (GameResponse가 Game 필드 없이 값만 평탄화해서 내려주는 것과 동일 원칙)

}
