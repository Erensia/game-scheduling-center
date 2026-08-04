package com.erensia.gamescheduling.weeklycontent;

import java.time.LocalDate;
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

	private final Long id;
	private final Long gameId;
	private final String name;
	private final LocalDate lastCompletedWeekStart;
	private final boolean completedThisWeek;

	public static WeeklyContentResponse from(WeeklyContent weeklyContent, boolean completedThisWeek) {
		return new WeeklyContentResponse(weeklyContent.getId(), weeklyContent.getGame().getId(),
				weeklyContent.getName(), weeklyContent.getLastCompletedWeekStart(),
				completedThisWeek);
	}

}
