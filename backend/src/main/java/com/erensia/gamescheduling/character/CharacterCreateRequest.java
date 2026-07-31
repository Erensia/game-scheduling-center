package com.erensia.gamescheduling.character;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * POST /games/{gameId}/characters 요청 바디.
 * 04-api-spec.md: "캐릭터 생성 (템플릿 적용 시 templateId 포함 가능)"
 *
 * GameCreateRequest와 동일한 이유로 가변(Setter)으로 간다: Jackson이 기본 생성자로
 * 빈 객체를 만든 뒤 setter로 채우는 방식이라, final 필드로는 역직렬화가 안 됨.
 *
 * templateId는 CharacterTemplate 도메인이 아직 구현되기 전이라 필드만 받아두고
 * Service로는 넘기지 않는다 (다음 사이클에서 처리 예정).
 */
@Getter
@Setter
@NoArgsConstructor
public class CharacterCreateRequest {

	@NotBlank
	private String name;

	private Long templateId;

}
