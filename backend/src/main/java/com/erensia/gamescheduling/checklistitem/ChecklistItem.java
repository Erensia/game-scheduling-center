package com.erensia.gamescheduling.checklistitem;

import com.erensia.gamescheduling.character.Character;
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
 * 캐릭터에 속한 체크리스트 항목 엔티티.
 * docs/backend/03-erd.md의 CHECKLIST_ITEM, docs/backend/02-domain-glossary.md 참고.
 *
 * 필드:
 *  - character : 소속 캐릭터 (Character 1 : N ChecklistItem). Character.java의 game 필드와
 *                동일한 패턴(@ManyToOne(FetchType.LAZY) + @JoinColumn(name = "character_id"))
 *  - text      : 자유 텍스트 항목 내용. 게임별 고유 용어(에코, 디스크 드라이브 등)가 여기에 들어감
 *  - done      : 완료 여부. PATCH /items/{itemId}로 토글
 *
 * 참고: id, createdAt은 BaseEntity에서 상속받으므로 여기서 다시 선언하지 않는다
 *      (Game, Character와 동일).
 */
@Entity
@Table(name = "checklist_items")
@Getter
@NoArgsConstructor
public class ChecklistItem extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "character_id", nullable = false)
	private Character character;

	@Column(nullable = false)
	private String text;

	@Column(nullable = false)
	private boolean done;

	public ChecklistItem(Character character, String text) {
		this.character = character;
		this.text = text;
	}

	/**
	 * done 상태를 반전시킨다. PATCH /items/{itemId}에서 사용.
	 * Character.toggleCompleted()와 완전히 동일한 패턴.
	 */
	public void toggleDone() {
		this.done = !this.done;
	}

}
