package com.erensia.gamescheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 파밍 관제소(Game Scheduling Center) 백엔드 진입점.
 *
 * {@code @EnableJpaAuditing}은 01-requirements.md 7절에서 결정한 대로
 * 전 엔티티에 {@code createdAt}을 자동으로 채워주기 위해 켜져 있다.
 * (updatedAt은 MVP 범위에서 도입하지 않기로 결정 - 같은 문서 참고)
 */
@SpringBootApplication
@EnableJpaAuditing
public class GameSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameSchedulingApplication.class, args);
	}

}
