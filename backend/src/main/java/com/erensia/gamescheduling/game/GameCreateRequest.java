package com.erensia.gamescheduling.game;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * POST /games 요청 바디. 04-api-spec.md 검증 규칙:
 * 이름 공백 불가 / resetDay 0~6 / partySize 1 이상
 *
 * 설계 결정 - 불변(final) 대신 가변으로 간다:
 * Jackson이 JSON을 객체로 역직렬화할 때는 기본 생성자로 빈 객체를 만든 뒤 setter로
 * 필드를 채우는 방식을 쓴다. final 필드 + @RequiredArgsConstructor(생성자 하나만 존재)
 * 조합으로는 Jackson이 객체를 만들 방법이 없어 InvalidDefinitionException이 발생한다
 * (Swagger POST 테스트 중 실제로 겪은 에러).
 * Response 쪽(GameResponse)은 서버 코드가 직접 new로 생성하고 Jackson은 직렬화(getter
 * 읽기)만 담당하므로 이 문제에서 자유롭고, 그래서 그쪽은 계속 불변으로 유지한다.
 */
@Getter
@Setter
@NoArgsConstructor
public class GameCreateRequest {

	@NotBlank
	private String name;

	@NotNull
	@Min(0)
	@Max(6)
	private Integer resetDay;

	@NotNull
	@Min(1)
	private Integer partySize;

}
