package com.erensia.gamescheduling.templateitem;

import com.erensia.gamescheduling.charactertemplate.CharacterTemplate;
import com.erensia.gamescheduling.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 템플릿에 속한 항목 엔티티.
 * docs/backend/03-erd.md의 TEMPLATE_ITEM, docs/backend/02-domain-glossary.md 참고.
 *
 * 채워야 할 필드 (ChecklistItem.java와 비교하며 작성할 것):
 *  - characterTemplate : 소속 템플릿 (CharacterTemplate 1 : N TemplateItem).
 *                         ChecklistItem.java의 character 필드와 동일한 패턴
 *                         (@ManyToOne(FetchType.LAZY) + @JoinColumn).
 *  - text               : 자유 텍스트 항목 내용.
 *
 * 참고(생각해볼 것): ChecklistItem에는 done 필드가 있지만, 03-erd.md의 TEMPLATE_ITEM
 * 정의에는 done 필드가 없다 (02-domain-glossary.md "저장 시점의 텍스트만 복사 (완료 상태는 없음)").
 * 템플릿은 "재사용 가능한 구성 세트"이지 실제 진행 상태를 추적하는 대상이 아니기 때문 -
 * 그래서 이 엔티티에는 toggleDone() 같은 상태 변경 메서드도 필요 없다.
 *
 * 참고: id, createdAt은 BaseEntity에서 상속받으므로 여기서 다시 선언하지 않는다.
 */
@Entity
@Table(name = "template_items")
@Getter
@NoArgsConstructor
public class TemplateItem extends BaseEntity {

	// TODO: characterTemplate 필드를 선언하세요.

	// TODO: text 필드를 선언하세요.

	public TemplateItem(CharacterTemplate characterTemplate, String text) {
		// TODO: 필드를 초기화하세요. ChecklistItem(Character, String) 생성자와 동일한 패턴입니다.
	}

}
