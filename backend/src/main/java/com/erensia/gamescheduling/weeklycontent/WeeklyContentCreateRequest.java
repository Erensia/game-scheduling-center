package com.erensia.gamescheduling.weeklycontent;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * POST /games/{gameId}/weekly 요청 바디. 04-api-spec.md: "주간 컨텐츠 추가 (name)"
 *
 * CharacterCreateRequest/ChecklistItemCreateRequest와 동일한 이유로 가변(Setter)으로 간다:
 * Jackson이 기본 생성자로 빈 객체를 만든 뒤 setter로 채우는 방식이라, final 필드로는 역직렬화가 안 됨.
 */
@Getter
@Setter
@NoArgsConstructor
public class WeeklyContentCreateRequest {

	@NotBlank
	private String name;

}
