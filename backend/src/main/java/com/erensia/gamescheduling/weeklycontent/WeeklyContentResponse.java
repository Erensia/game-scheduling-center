package com.erensia.gamescheduling.weeklycontent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * WeeklyContent 응답 DTO.
 * CharacterResponse/ChecklistItemResponse와 동일한 이유로 불변(final)을 유지한다 - 서버 코드가
 * from(...)에서 직접 생성하고, Jackson은 직렬화(getter만 읽음)만 담당하기 때문.
 * gameId는 weeklyContent.getGame().getId()로 평탄화해서 내려주고, Game 전체를 중첩시키지 않는다
 * (CharacterResponse.gameId와 동일한 패턴).
 *
 * completedThisWeek는 DB 컬럼이 아니라 04-api-spec.md에 명시된 계산값이다 - "GET /games/{gameId}/weekly
 * 응답에 completedThisWeek 계산값 포함". WeeklyContentService가 계산해서 파라미터로 넘겨줘야 하므로,
 * from(WeeklyContent) 하나만으로는 만들 수 없다는 점에 주의.
 */
@Getter
@RequiredArgsConstructor
public class WeeklyContentResponse {

	// TODO: 필드를 선언하세요 - id, gameId, name, lastCompletedWeekStart, completedThisWeek.
	//       타입은 각각 Long, Long, String, java.time.LocalDate, boolean.
	//       CharacterResponse의 필드 선언부(id, gameId, name, completed, items)와 같은 형태입니다.

	// TODO: from(WeeklyContent weeklyContent, boolean completedThisWeek) 정적 팩토리 메서드를 작성하세요.
	//       CharacterResponse.from(Character character)와 달리 파라미터가 하나 더 필요합니다 -
	//       completedThisWeek는 엔티티 필드가 아니라 서비스 레이어가 계산해 넘겨주는 값이기 때문입니다.
	//       weeklyContent.getGame().getId()로 gameId를 평탄화하는 것도 잊지 마세요.

}
