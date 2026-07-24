package com.erensia.gamescheduling.game;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Game 엔티티의 데이터 접근 계층.
 * JpaRepository<Game, Long>을 상속하는 것만으로 save(), findById(), findAll(),
 * deleteById() 등 기본 CRUD가 별도 구현 없이 런타임에 자동 제공된다.
 */
public interface GameRepository extends JpaRepository<Game, Long> {

}
