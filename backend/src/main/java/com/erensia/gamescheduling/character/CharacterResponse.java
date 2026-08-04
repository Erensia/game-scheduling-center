package com.erensia.gamescheduling.character;

import com.erensia.gamescheduling.checklistitem.ChecklistItem;
import com.erensia.gamescheduling.checklistitem.ChecklistItemResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Character 응답 DTO.
 * GameResponse와 동일한 이유로 불변(final)을 유지한다 - 서버 코드가 from(character)에서
 * 직접 생성하고, Jackson은 직렬화(getter만 읽음)만 담당하기 때문.
 * gameId는 character.getGame().getId()로 평탄화해서 내려주고, Game 전체를 중첩시키지 않는다.
 * items는 character.getChecklistItems()를 순회해 ChecklistItemResponse로 변환한 목록이다 -
 * 02-domain-glossary.md에서 Character.checklistItems가 1:N으로 정의돼 있고, 04-api-spec.md에
 * ChecklistItem 전용 GET 엔드포인트가 없는 이유가 바로 이 중첩 응답이기 때문(별도 조회 소비처가 없음).
 *
 * 주의(미해결): character.getChecklistItems()는 LAZY 컬렉션이다. spring.jpa.open-in-view가
 * false로 꺼져 있어서, 이 메서드가 트랜잭션/영속성 컨텍스트가 닫힌 뒤에(즉 컨트롤러에서) 호출되면
 * LazyInitializationException이 날 수 있다. CharacterService/CharacterRepository 레벨에서
 * 해결 필요 - 이 파일만 고쳐서는 해결되지 않는다.
 */
@Getter
@RequiredArgsConstructor
public class CharacterResponse {

	private final Long id;
	private final Long gameId;
	private final String name;
	private final boolean completed;
	private final List<ChecklistItemResponse> items;

	public static CharacterResponse from(Character character) {
		List<ChecklistItemResponse> responses = new ArrayList<>();
		List<ChecklistItem> checklistItems = character.getChecklistItems();
		for (ChecklistItem checklistItem : checklistItems) {
			responses.add(ChecklistItemResponse.from(checklistItem));
		}
		return new CharacterResponse(character.getId(), character.getGame().getId(),
				character.getName(), character.isCompleted(), responses);
	}

}
