package com.erensia.gamescheduling.character;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Character 엔티티의 데이터 접근 계층.
 * GameRepository와 마찬가지로 JpaRepository 상속만으로 기본 CRUD는 자동 제공된다.
 * 여기서는 게임별 목록 조회를 위한 쿼리 메서드만 추가로 선언한다.
 * 정렬 기준은 id 오름차순으로 통일한다 (03-erd.md 참고, sortOrder 컬럼 없음).
 */
public interface CharacterRepository extends JpaRepository<Character, Long> {

	List<Character> findByGameIdOrderByIdAsc(Long gameId);

	List<Character> findByGameIdAndCompletedOrderByIdAsc(Long gameId, boolean completed);

}
