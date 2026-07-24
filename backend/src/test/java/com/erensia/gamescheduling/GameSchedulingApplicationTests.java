package com.erensia.gamescheduling;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 스프링 컨텍스트가 정상적으로 뜨는지(빈 설정 오류, DB 연결 등)를 확인하는
 * 가장 기본적인 스모크 테스트. 로컬 PostgreSQL(dev 프로파일)이 떠 있어야 통과한다.
 */
@SpringBootTest
class GameSchedulingApplicationTests {

	@Test
	void contextLoads() {
	}

}
