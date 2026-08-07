package com.erensia.gamescheduling.templateitem;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CharacterTemplateCreateRequest.items에 담기는 항목 하나를 표현하는 DTO.
 * 04-api-spec.md: "items: [{text}]"
 *
 * ChecklistItemCreateRequest와 동일한 이유로 가변(Setter)으로 간다: Jackson이 기본 생성자로
 * 빈 객체를 만든 뒤 setter로 채우는 방식이라, final 필드로는 역직렬화가 안 됨.
 * 이 DTO는 독립된 엔드포인트 요청 바디가 아니라, CharacterTemplateCreateRequest.items
 * 리스트의 원소로만 쓰인다는 점에서 ChecklistItemCreateRequest와 차이가 있다.
 */
@Getter
@Setter
@NoArgsConstructor
public class TemplateItemCreateRequest {

	// TODO: text 필드를 선언하세요 (@NotBlank). ChecklistItemCreateRequest.text 참고.

}
