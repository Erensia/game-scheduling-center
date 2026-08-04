package com.erensia.gamescheduling.weeklycontent;

import com.erensia.gamescheduling.common.BaseEntity;
import com.erensia.gamescheduling.game.Game;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임에 속한, 매주 리셋되는 완료 대상 컨텐츠 엔티티.
 * docs/backend/03-erd.md의 WEEKLY_CONTENT, docs/backend/02-domain-glossary.md 참고.
 *
 * 필드:
 *  - game                   : 소속 게임 (Game 1 : N WeeklyContent). Character.java의 game 필드와
 *                             동일한 패턴(@ManyToOne(FetchType.LAZY) + @JoinColumn(name = "game_id")).
 *  - name                   : 컨텐츠 이름 (예: "주간 보스", "긴급 임무"). 수정 API 없음 - Game.name과 동일하게
 *                             생성 후 불변으로 취급 (04-api-spec.md에 PATCH 없음).
 *  - lastCompletedWeekStart : 완료 처리된 "주 시작일". null이면 이번 리셋 주기 기준 아직 완료 안 한 상태.
 *                             "이번 주 완료 여부(completedThisWeek)"는 이 값을 저장해두고, DB에는 없는
 *                             계산값으로 매 조회 시 서비스 레이어에서 구한다 (03-erd.md 설계 메모 참고).
 *
 * 참고: id, createdAt은 BaseEntity에서 상속받으므로 여기서 다시 선언하지 않는다 (Game, Character와 동일).
 */
@Entity
@Table(name = "weekly_contents")
@Getter
@NoArgsConstructor
public class WeeklyContent extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	@Column(nullable = false)
	private String name;

	@Column(nullable = true)
	private LocalDate lastCompletedWeekStart;

	// TODO: Game game, String name을 받는 생성자를 작성하세요.
	// Character(Game game, String name) 생성자(Character.java 참고)와 완전히 동일한 패턴입니다.
	// lastCompletedWeekStart는 생성 시점엔 "아직 완료 안 함" 상태이므로 필드 기본값(null) 그대로 둡니다.
	public WeeklyContent(Game game, String name) {
		this.game = game;
		this.name = name;
	}

	// TODO: 완료 상태를 토글하는 메서드를 작성하세요.
	// Character.toggleCompleted(), ChecklistItem.toggleDone()과 이 도메인의 차이점:
	// 그 둘은 boolean 필드를 그냥 반전(!)시키면 끝이지만, 이 도메인은 "언제 완료했는지" 날짜 자체를
	// 저장하는 구조라 단순 반전이 아닙니다.
	// - 메서드가 "이번 주 시작일"을 파라미터로 받는다고 가정하세요 (예: LocalDate currentWeekStart).
	//   엔티티가 직접 오늘 날짜나 resetDay 계산 로직을 알 필요는 없습니다 - 그건 서비스 레이어의 책임입니다.
	// - lastCompletedWeekStart가 null이면(=미완료) currentWeekStart로 채워 "완료 처리".
	// - lastCompletedWeekStart에 이미 값이 있으면(=완료됨) null로 되돌려 "완료 취소".
	// - if/else로 분기하세요 (람다·삼항연산자 남발 금지, 이 프로젝트 컨벤션).
	public void toggleWeeklyContent(LocalDate currentWeekStart) {
		if (this.lastCompletedWeekStart == null) {
			this.lastCompletedWeekStart = currentWeekStart;
		} else {
			this.lastCompletedWeekStart = null;
		}
	}

}
