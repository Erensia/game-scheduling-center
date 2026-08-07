package com.erensia.gamescheduling.charactertemplate;

import com.erensia.gamescheduling.templateitem.TemplateItemCreateRequest;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * POST /games/{gameId}/templates 요청 바디.
 * 04-api-spec.md: "템플릿 저장 (name, items: [{text}])"
 *
 * CharacterCreateRequest와 동일한 이유로 가변(Setter)으로 간다: Jackson이 기본 생성자로
 * 빈 객체를 만든 뒤 setter로 채우는 방식이라, final 필드로는 역직렬화가 안 됨.
 */
@Getter
@Setter
@NoArgsConstructor
public class CharacterTemplateCreateRequest {

	// TODO: name 필드를 선언하세요 (@NotBlank).

	// TODO: items 필드를 선언하세요. 타입은 List<TemplateItemCreateRequest>.
	//   중첩된 각 항목까지 검증(@NotBlank)이 걸리게 하려면, 이 필드에 @Valid도 함께
	//   붙여야 하는지 jakarta.validation 문서를 참고해서 판단하세요.

}
