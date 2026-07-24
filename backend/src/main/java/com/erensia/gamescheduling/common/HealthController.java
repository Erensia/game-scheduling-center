package com.erensia.gamescheduling.common;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 개발 환경(Java/Gradle/Spring Boot/PostgreSQL 연결)이 제대로 준비됐는지
 * 빠르게 확인하기 위한 최소 엔드포인트. 도메인 기능이 하나도 없어도
 * 이 엔드포인트 하나만으로 "서버가 뜨는지 / DB에 연결되는지"를 확인할 수 있다.
 *
 * 실제 도메인 컨트롤러(게임/캐릭터/...)가 갖춰지면 이 클래스는 지워도 무방하다.
 */
@RestController
public class HealthController {

	@GetMapping("/health")
	public Map<String, String> health() {
		return Map.of("status", "OK");
	}

}
