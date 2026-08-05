package com.erensia.gamescheduling.game;

import com.erensia.gamescheduling.character.Character;
import com.erensia.gamescheduling.common.BaseEntity;
import com.erensia.gamescheduling.weeklycontent.WeeklyContent;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임(명조, 젠존제 등) 엔티티.
 * docs/backend/03-erd.md의 GAME, docs/backend/02-domain-glossary.md 참고.
 *
 * 필드:
 *  - name        : 게임 이름. 생성 후 수정 불가 (04-api-spec.md 결정 - PATCH에 이름 없음)
 *  - resetDay    : 주간 리셋 요일 (0=일요일 ~ 6=토요일). 생성 후 PATCH로 변경 가능
 *  - partySize   : 새로 만드는 파티에 적용될 인원수. 생성 후 PATCH로 변경 가능
 *                  (이미 만들어진 Party에는 영향 없음 - Party가 자신의 partySize를 스냅샷으로 저장하기 때문.
 *                   이 결정은 01-requirements.md 7절 참고)
 *  - characters  : 소속 캐릭터 목록 (Character가 game 필드로 소유). 삭제 정책(2026-07-31 결정):
 *                  Game 삭제 시 소속 Character를 전부 연쇄 삭제한다. DB 레벨 FK cascade가 아니라
 *                  JPA 레벨(cascade = CascadeType.REMOVE, orphanRemoval = true)로 처리한다 -
 *                  GameService.deleteGame()이 쓰는 gameRepository.deleteById()가 내부적으로
 *                  findById 후 delete(entity)를 호출하므로 이 설정만으로 cascade가 정상 동작한다.
 *                  (03-erd.md 설계 메모, CharacterIntegrationTest 참고)
 *  - weeklyContents : 소속 주간 컨텐츠 목록 (WeeklyContent가 game 필드로 소유). characters와 완전히
 *                  같은 이유·같은 패턴(cascade = CascadeType.REMOVE, orphanRemoval = true)으로
 *                  Game 삭제 시 소속 WeeklyContent를 전부 연쇄 삭제한다 (03-erd.md "Game 1 : N
 *                  WeeklyContent, 게임 삭제 시 함께 삭제" 규칙). GameService.deleteGame()의 기존
 *                  gameRepository.deleteById() 호출 하나로 WeeklyContent까지 cascade 삭제된다.
 */
@Entity
@Table(name = "games")
@Getter
@NoArgsConstructor
public class Game extends BaseEntity {

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer resetDay;

	@Column(nullable = false)
	private Integer partySize;

	@OneToMany(mappedBy = "game", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Character> characters = new ArrayList<>();
	
	@OneToMany(mappedBy = "game", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<WeeklyContent> weeklyContents = new ArrayList<>();

	public Game(String name, Integer resetDay, Integer partySize) {
		this.name = name;
		this.resetDay = resetDay;
		this.partySize = partySize;
	}

	/**
	 * 게임 설정(리셋 요일, 파티 인원수) 수정.
	 * name은 생성 후 수정 불가하기로 결정했으므로 파라미터로 받지 않는다.
	 */
	public void updateSettings(Integer resetDay, Integer partySize) {
		this.resetDay = resetDay;
		this.partySize = partySize;
	}

}
