package com.erensia.gamescheduling.weeklycontent;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * WeeklyContent 엔티티의 데이터 접근 계층.
 * CharacterRepository/ChecklistItemRepository와 마찬가지로 JpaRepository 상속만으로
 * 기본 CRUD(save, findById, deleteById 등)는 자동 제공된다.
 *
 * WeeklyContent는 Character와 달리 LAZY 컬렉션 필드가 없으므로(자식 엔티티를 갖지 않음),
 * CharacterRepository처럼 LEFT JOIN FETCH를 쓰는 커스텀 @Query가 필요 없다 -
 * 파생 쿼리(메서드 이름 기반)만으로 충분하다.
 */
public interface WeeklyContentRepository extends JpaRepository<WeeklyContent, Long> {

	List<WeeklyContent> findByGameIdOrderByIdAsc(Long gameId);

}
