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
 */
@Getter
@Setter
@NoArgsConstructor
public class CharacterCreateRequest {

	// TODO: name 필드
	//  - @NotBlank
	//  - private String name;

	// TODO: templateId 필드 (선택 사항 - 검증 애너테이션 없음, null 허용)
	//  - private Long templateId;
	//  - 주의: CharacterTemplate 도메인이 아직 구현 전이므로, 이번 사이클에서는
	//          필드만 받아두고 Service로 넘기지 않아도 됨 (다음 사이클에서 처리)

}
