package com.erensia.gamescheduling.weeklycontent;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * WeeklyContent 엔티티의 데이터 접근 계층.
 * CharacterRepository/ChecklistItemRepository와 마찬가지로 JpaRepository 상속만으로
 * 기본 CRUD(save, findById, deleteById 등)는 자동 제공된다.
 *
 * WeeklyContent는 Character처럼 LAZY 자식 컬렉션을 갖진 않지만, WeeklyContent.game 자체가
 * @ManyToOne(FetchType.LAZY) 참조다. 목록 조회(getWeeklyContents) 후 컨트롤러가 항목마다
 * WeeklyContentService.isCompletedThisWeek() -> calculateWeekStart()에서 game.getResetDay()를
 * 늦게(리포지토리 트랜잭션이 끝난 뒤) 참조하다가 세션이 이미 닫혀 LazyInitializationException이
 * 발생했다(통합 테스트로 확인됨). 그래서 파생 쿼리 대신 JOIN FETCH로 game을 함께 로딩한다.
 * game은 nullable = false라 결과 행 중복 걱정이 없으므로 LEFT JOIN이 아닌 JOIN으로 충분하다.
 */
public interface WeeklyContentRepository extends JpaRepository<WeeklyContent, Long> {

	@Query("SELECT c FROM WeeklyContent c JOIN FETCH c.game "
			+ "WHERE c.game.id = :gameId ORDER BY c.id ASC")
	List<WeeklyContent> findByGameIdOrderByIdAsc(Long gameId);

}
