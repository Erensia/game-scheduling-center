package com.erensia.gamescheduling.game;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PATCH /games/{gameId} 요청 바디.
 * name 필드는 포함하지 않는다 - 이름 수정은 04-api-spec.md에서 MVP 범위 밖으로 결정됨.
 *
 * 설계 결정 - 불변(final) 대신 가변으로 간다:
 * Jackson이 JSON을 객체로 역직렬화할 때는 기본 생성자로 빈 객체를 만든 뒤 setter로
 * 필드를 채우는 방식을 쓴다. final 필드 + @RequiredArgsConstructor(생성자 하나만 존재)
 * 조합으로는 Jackson이 객체를 만들 방법이 없어 InvalidDefinitionException이 발생한다
 * (GameCreateRequest에서 실제로 겪은 에러와 동일한 원인).
 * Response 쪽(GameResponse)은 서버 코드가 직접 new로 생성하고 Jackson은 직렬화(getter
 * 읽기)만 담당하므로 이 문제에서 자유롭고, 그래서 그쪽은 계속 불변으로 유지한다.
 */
@Getter
@Setter
@NoArgsConstructor
public class GameUpdateRequest {

	@NotNull
	@Min(0)
	@Max(6)
	private Integer resetDay;

	@NotNull
	@Min(1)
	private Integer partySize;

}
