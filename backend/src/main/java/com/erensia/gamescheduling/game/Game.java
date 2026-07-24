package com.erensia.gamescheduling.game;

import com.erensia.gamescheduling.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
