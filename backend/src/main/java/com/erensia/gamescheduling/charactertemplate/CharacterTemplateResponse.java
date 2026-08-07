package com.erensia.gamescheduling.charactertemplate;

import com.erensia.gamescheduling.templateitem.TemplateItem;
import com.erensia.gamescheduling.templateitem.TemplateItemResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * CharacterTemplate 응답 DTO.
 * CharacterResponse와 동일한 이유로 불변(final)을 유지한다 - 서버 코드가 from(characterTemplate)에서
 * 직접 생성하고, Jackson은 직렬화(getter만 읽음)만 담당하기 때문.
 * gameId는 characterTemplate.getGame().getId()로 평탄화해서 내려주고, Game 전체를 중첩시키지 않는다.
 * items는 characterTemplate.getTemplateItems()를 순회해 TemplateItemResponse로 변환한 목록이다.
 */
@Getter
@RequiredArgsConstructor
public class CharacterTemplateResponse {

	// TODO: id, gameId, name, items(List<TemplateItemResponse>) 필드를 선언하세요.
	//   CharacterResponse의 필드 구성과 동일한 패턴입니다.

	public static CharacterTemplateResponse from(CharacterTemplate characterTemplate) {
		// TODO: CharacterResponse.from(character)와 동일한 패턴으로 구현하세요.
		//   1) List<TemplateItemResponse> 빈 목록을 만든다.
		//   2) characterTemplate.getTemplateItems()를 for문으로 순회하며(람다 금지)
		//      TemplateItemResponse.from(item)을 만들어 목록에 추가한다.
		//   3) new CharacterTemplateResponse(id, gameId, name, 목록)을 반환한다.
		return null;
	}

}
