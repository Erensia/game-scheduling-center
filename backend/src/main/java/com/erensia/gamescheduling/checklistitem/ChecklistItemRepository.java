package com.erensia.gamescheduling.checklistitem;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ChecklistItem 엔티티의 데이터 접근 계층.
 * CharacterRepository와 동일한 패턴: JpaRepository 상속만으로 기본 CRUD는 자동 제공된다.
 * 정렬 기준은 id 오름차순으로 통일한다 (03-erd.md 참고, sortOrder 컬럼 없음, 재정렬 기능 미지원 - 04-api-spec.md TBD 참고).
 *
 * 별도 목록 조회 쿼리 메서드는 두지 않는다: ChecklistItem 전용 GET 엔드포인트가 없고
 * (01-requirements.md FR-9, 05-migration-plan.md 참고), CharacterResponse.items로 중첩
 * 응답하는 쪽은 CharacterRepository의 LEFT JOIN FETCH 쿼리가 이미 담당하기 때문이다
 * (CharacterRepository.java 참고).
 */
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {

}
