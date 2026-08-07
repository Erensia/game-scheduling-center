package com.erensia.gamescheduling.templateitem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * TemplateItem 응답 DTO.
 * ChecklistItemResponse와 동일한 이유로 불변(final)을 유지한다 - 서버 코드가 from(item)에서
 * 직접 생성하고, Jackson은 직렬화(getter만 읽음)만 담당하기 때문.
 * templateId는 item.getCharacterTemplate().getId()로 평탄화해서 내려주고,
 * CharacterTemplate 전체를 중첩시키지 않는다.
 */
@Getter
@RequiredArgsConstructor
public class TemplateItemResponse {

	// TODO: id, templateId, text 필드를 선언하세요. ChecklistItemResponse의 필드 구성과 동일한 패턴입니다.

	public static TemplateItemResponse from(TemplateItem item) {
		// TODO: ChecklistItemResponse.from(item)과 동일한 패턴으로 구현하세요.
		return null;
	}

}
