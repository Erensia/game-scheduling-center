package com.erensia.gamescheduling.character;

import com.erensia.gamescheduling.common.BaseEntity;
import com.erensia.gamescheduling.game.Game;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임에 속한 육성 대상 캐릭터 엔티티.
 * docs/backend/03-erd.md의 CHARACTER, docs/backend/02-domain-glossary.md 참고.
 *
 * 필드:
 *  - game      : 소속 게임 (Game 1 : N Character, Game 삭제 시 cascade 삭제)
 *  - name      : 캐릭터 이름. 이름 수정은 04-api-spec.md에서 MVP 범위 밖으로 결정됨
 *  - completed : "더 이상 파밍 안 해도 됨" 표시. PATCH /characters/{characterId}로 토글
 *
 * 참고: id, createdAt은 BaseEntity에서 상속받으므로 여기서 다시 선언하지 않는다 (Game과 동일).
 * 참고: checklistItems는 이번 사이클에서 다루지 않는다 - ChecklistItem 엔티티가 아직 없으므로
 *       필드를 추가하지 않았다. 다음 사이클(체크리스트 항목 도메인)에서 @OneToMany(mappedBy = "character")로 추가 예정.
 */
@Entity
@Table(name = "characters")
@Getter
@NoArgsConstructor
public class Character extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private boolean completed;

	public Character(Game game, String name) {
		this.game = game;
		this.name = name;
	}

	/**
	 * completed 상태를 반전시킨다. PATCH /characters/{characterId}에서 사용.
	 */
	public void toggleCompleted() {
		this.completed = !this.completed;
	}

}
