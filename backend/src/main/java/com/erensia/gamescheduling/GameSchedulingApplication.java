package com.erensia.gamescheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 파밍 관제소(Game Scheduling Center) 백엔드 진입점.
 *
 * JPA Auditing 설정({@code @EnableJpaAuditing})은 config.JpaAuditingConfig로 분리돼 있다.
 * 이유: 메인 클래스에 직접 붙이면 @WebMvcTest 같은 슬라이스 테스트에서도 타입 필터 없이
 * 항상 로딩되는 바람에, @Entity 스캔이 빠진 슬라이스에서 JpaAuditingHandler 생성이 실패하는
 * 문제가 있었다 (2026-07-29 트러블슈팅 참고, config.JpaAuditingConfig 클래스 주석 참고).
 */
@SpringBootApplication
public class GameSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameSchedulingApplication.class, args);
	}

}
