package com.erensia.gamescheduling.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 설정. 01-requirements.md 7절 결정대로 전 엔티티에 {@code createdAt}을
 * 자동으로 채워주기 위해 켜져 있다 (updatedAt은 MVP 범위에서 미도입 - 같은 문서 참고).
 *
 * 원래 GameSchedulingApplication(메인 클래스)에 붙어 있었으나, 슬라이스 테스트(@WebMvcTest 등)에서
 * 메인 클래스는 타입 필터링 없이 항상 설정 소스로 로딩되는 탓에, @Entity 스캔이 빠진 슬라이스에서도
 * JpaAuditingHandler가 만들어지려다 "JPA metamodel must not be empty" 에러로 깨지는 문제가 있었다
 * (2026-07-29 GameControllerTest 트러블슈팅 참고).
 *
 * 별도의 일반 @Configuration 클래스로 분리하면 @WebMvcTest의 컴포넌트 스캔 필터(컨트롤러/어드바이스 외
 * 제외)에 걸려 슬라이스 컨텍스트에는 로딩되지 않고, @SpringBootTest(통합 테스트)나 실제 애플리케이션
 * 구동 시에는 컴포넌트 스캔으로 정상적으로 포함된다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

}
