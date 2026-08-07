package com.erensia.gamescheduling.charactertemplate;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * CharacterTemplate 엔티티의 데이터 접근 계층.
 * CharacterRepository/WeeklyContentRepository와 마찬가지로 JpaRepository 상속만으로
 * 기본 CRUD(save, findById, deleteById, existsById)는 자동 제공된다.
 *
 * TODO: 게임별 템플릿 목록 조회 메서드가 필요하다 (GET /games/{gameId}/templates).
 *   CharacterRepository.findByGameIdOrderByIdAsc()를 참고할 것 - templateItems를
 *   LEFT JOIN FETCH로 함께 로딩해야, 컨트롤러 단(CharacterTemplateResponse.from())에서
 *   LazyInitializationException 없이 항목 목록을 읽을 수 있다
 *   (spring.jpa.open-in-view=false 환경이라 파생 쿼리만으로는 부족함 - CharacterRepository.java
 *   상단 주석 참고). DISTINCT로 컬렉션 조인에 의한 행 중복도 제거할 것.
 *   정렬은 id 오름차순(ORDER BY t.id ASC)으로 통일한다 (03-erd.md, sortOrder 컬럼 없음).
 */
public interface CharacterTemplateRepository extends JpaRepository<CharacterTemplate, Long> {

	// TODO: List<CharacterTemplate> findByGameIdOrderByIdAsc(Long gameId); 를
	//       @Query와 함께 선언하세요.

}
