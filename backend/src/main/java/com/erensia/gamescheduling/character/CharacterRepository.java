package com.erensia.gamescheduling.character;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Character 엔티티의 데이터 접근 계층.
 * GameRepository와 마찬가지로 JpaRepository 상속만으로 기본 CRUD는 자동 제공된다.
 * 여기서는 게임별 목록 조회를 위한 쿼리 메서드만 추가로 선언한다.
 * 정렬 기준은 id 오름차순으로 통일한다 (03-erd.md 참고, sortOrder 컬럼 없음).
 *
 * 아래 조회 메서드들은 파생 쿼리(메서드 이름 기반) 대신 JPQL @Query로 checklistItems를
 * LEFT JOIN FETCH해서 즉시 로딩한다 - spring.jpa.open-in-view=false 환경에서
 * character.getChecklistItems()(LAZY)를 컨트롤러 단(CharacterResponse.from())에서 읽으면
 * 세션이 이미 닫혀 있어 LazyInitializationException이 나기 때문. LEFT를 쓰는 이유는 체크리스트가
 * 하나도 없는 캐릭터가 결과에서 빠지지 않게 하기 위함이고, DISTINCT는 컬렉션 조인으로 인한
 * Character 행 중복을 걸러내기 위함이다. :파라미터명 바인딩은 build.gradle의
 * compilerArgs '-parameters' 설정 덕분에 @Param 없이도 동작한다.
 */
public interface CharacterRepository extends JpaRepository<Character, Long> {

	@Query("SELECT DISTINCT c FROM Character c LEFT JOIN FETCH c.checklistItems "
			+ "WHERE c.game.id = :gameId ORDER BY c.id ASC")
	List<Character> findByGameIdOrderByIdAsc(Long gameId);

	@Query("SELECT DISTINCT c FROM Character c LEFT JOIN FETCH c.checklistItems "
			+ "WHERE c.game.id = :gameId AND c.completed = :completed ORDER BY c.id ASC")
	List<Character> findByGameIdAndCompletedOrderByIdAsc(Long gameId, boolean completed);

	@Query("SELECT DISTINCT c FROM Character c LEFT JOIN FETCH c.checklistItems "
			+ "WHERE c.id = :id")
	Optional<Character> findByIdWithChecklistItems(Long id);

}
