package com.erensia.gamescheduling.character;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Character 엔티티의 데이터 접근 계층.
 * GameRepository와 마찬가지로 JpaRepository 상속만으로 기본 CRUD는 자동 제공된다.
 * 여기서는 게임별 목록 조회를 위한 쿼리 메서드만 추가로 선언한다.
 */
public interface CharacterRepository extends JpaRepository<Character, Long> {

	// TODO: 게임 ID로 캐릭터 전체 목록 조회 (id 오름차순 정렬)
	//  - List<Character> findByGameIdOrderByIdAsc(Long gameId);
	//  - 정렬 기준은 id ASC로 통일하기로 결정됨 (03-erd.md 참고, sortOrder 컬럼 없음)
	//  - 주의: Character.game 필드명이 "game"이므로, Spring Data JPA 쿼리 메서드 이름 규칙상
	//          연관 엔티티의 id를 참조할 때는 "GameId"로 표기 (game.getId()가 아니라 필드 경로 game.id 기준)

	// TODO: 게임 ID + completed 여부로 필터링 조회 (id 오름차순 정렬)
	//  - List<Character> findByGameIdAndCompletedOrderByIdAsc(Long gameId, boolean completed);
	//  - GET /games/{gameId}/characters?completed=true/false 대응

}
