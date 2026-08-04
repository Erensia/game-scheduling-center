package com.erensia.gamescheduling.checklistitem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ChecklistItem 응답 DTO.
 * CharacterResponse와 동일한 이유로 불변(final)을 유지한다 - 서버 코드가 from(item)에서
 * 직접 생성하고, Jackson은 직렬화(getter만 읽음)만 담당하기 때문.
 * characterId는 item.getCharacter().getId()로 평탄화해서 내려주고, Character 전체를 중첩시키지 않는다.
 */
@Getter
@RequiredArgsConstructor
public class ChecklistItemResponse {

	private final Long id;
	private final Long characterId;
	private final String text;
	private final boolean done;

	public static ChecklistItemResponse from(ChecklistItem item) {
		return new ChecklistItemResponse(item.getId(), item.getCharacter().getId(),
				item.getText(), item.isDone());
	}

}
