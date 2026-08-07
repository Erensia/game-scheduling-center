package com.erensia.gamescheduling.charactertemplate;

import com.erensia.gamescheduling.common.BaseEntity;
import com.erensia.gamescheduling.game.Game;
import com.erensia.gamescheduling.templateitem.TemplateItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임에 속한 캐릭터 육성 템플릿 엔티티.
 * docs/backend/03-erd.md의 CHARACTER_TEMPLATE, docs/backend/02-domain-glossary.md 참고.
 *
 * 채워야 할 필드 (Character.java, WeeklyContent.java와 비교하며 작성할 것):
 *  - game          : 소속 게임 (Game 1 : N CharacterTemplate). Character.java의 game 필드와
 *                    완전히 동일한 패턴 (@ManyToOne(FetchType.LAZY) + @JoinColumn(name = "game_id")).
 *  - name          : 템플릿 이름.
 *  - templateItems : 소속 템플릿 항목 목록 (CharacterTemplate 1 : N TemplateItem).
 *
 * TODO(templateItems의 cascade 옵션을 정할 때 반드시 생각해볼 것):
 *   Character.checklistItems는 cascade = CascadeType.REMOVE만 쓴다 - 캐릭터 생성(POST)과
 *   체크리스트 항목 추가(POST)가 서로 다른 두 개의 엔드포인트로 분리되어 있어서, 항목은
 *   ChecklistItemRepository.save()로 "따로" 저장되기 때문이다 (부모 저장 시 자식이 함께
 *   insert될 필요가 없음).
 *   반면 04-api-spec.md의 템플릿 API는 "POST /games/{gameId}/templates (name, items: [{text}])"
 *   하나뿐이다 - 즉 템플릿과 그 항목들이 "같은 순간에 함께" 생성된다. 이 차이가 cascade 옵션
 *   선택에 어떤 영향을 주는지 (PERSIST가 필요한지, REMOVE만으로 충분한지) 판단해서 채울 것.
 *   orphanRemoval, fetch(LAZY) 여부도 Character.checklistItems 패턴과 비교해서 정할 것.
 *
 * 참고: id, createdAt은 BaseEntity에서 상속받으므로 여기서 다시 선언하지 않는다.
 */
@Entity
@Table(name = "character_templates")
@Getter
@NoArgsConstructor
public class CharacterTemplate extends BaseEntity {

	// TODO: game 필드를 선언하세요.

	// TODO: name 필드를 선언하세요.

	// TODO: templateItems 필드를 선언하세요. 초기값은 new ArrayList<>()로 주세요
	//       (Character.checklistItems, Game.characters와 동일한 이유).

	public CharacterTemplate(Game game, String name) {
		// TODO: 필드를 초기화하세요. Character(Game, String), WeeklyContent(Game, String)
		//       생성자와 동일한 패턴입니다.
	}

	// TODO: 템플릿 항목을 컬렉션에 추가하는 편의 메서드가 필요한지 생각해보세요.
	//       (CharacterTemplateService에서 TemplateItem을 어떻게 구성해 저장할지에 따라
	//       필요 여부가 달라집니다 - 필수 아님)

}
