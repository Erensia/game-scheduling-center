package com.erensia.gamescheduling.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Game 도메인 통합 테스트.
 *
 * GameServiceTest(서비스 단독), GameControllerTest(컨트롤러 단독, 서비스는 mock)와 달리
 * 여기서는 Controller -> Service -> Repository -> 실제 DB까지 전부 진짜로 붙여서 검증한다.
 * 즉 "이 API를 실제로 호출하면 DB까지 다녀와서 정말로 기대한 대로 동작하는가"를 확인하는 마지막 단계.
 *
 * @Testcontainers + @Container + PostgreSQLContainer: 테스트 실행 시 도커로 진짜 PostgreSQL을
 * 하나 띄운다. 06-tech-stack.md에서 "H2 대신 운영과 동일 엔진"을 쓰기로 결정한 것과 같은 이유 -
 * 통합 테스트도 실제로 배포될 엔진과 같은 PostgreSQL로 돌려야 SQL 방언 차이로 인한
 * "테스트는 통과했는데 운영에서 깨지는" 상황을 막을 수 있다.
 * @ServiceConnection이 붙어 있으면 application-dev.yml의 datasource 설정 대신
 * 이 컨테이너의 접속 정보를 스프링이 자동으로 사용한다 (application-*.yml 수정 불필요).
 *
 * 사전 준비: 로컬에 Docker Desktop(또는 호환 엔진)이 떠 있어야 이 테스트가 실행된다.
 * (docs/backend/06-tech-stack.md의 "회사 컴: Docker Desktop 컨테이너" 환경과 동일한 전제)
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class GameIntegrationTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private GameRepository gameRepository;

	@BeforeEach
	void cleanUp() {
		gameRepository.deleteAll();
	}

	@Test
	void 게임을_생성하면_목록_조회에서_확인할_수_있다() throws Exception {
		GameCreateRequest request = new GameCreateRequest();

		request.setName("ww");
		request.setResetDay(1);
		request.setPartySize(3);

		mockMvc.perform(post("/games").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("ww"))
				.andExpect(jsonPath("$.resetDay").value(1))
				.andExpect(jsonPath("$.partySize").value(3));

		mockMvc.perform(get("/games"))
				.andExpect(jsonPath("$[0].name").value(request.getName()))
				.andExpect(jsonPath("$[0].resetDay").value(request.getResetDay()))
				.andExpect(jsonPath("$[0].partySize").value(request.getPartySize()));
	}

	@Test
	void 존재하지_않는_게임을_수정하면_404_GAME_NOT_FOUND를_반환한다() throws Exception {
		GameUpdateRequest request = new GameUpdateRequest();

		request.setResetDay(3);
		request.setPartySize(4);

		mockMvc.perform(patch("/games/{gameId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("GAME_NOT_FOUND"));
	}

	@Test
	void 게임을_삭제하면_DB에서도_실제로_사라진다() throws Exception {
		gameRepository.save(new Game("ww", 1, 3));

		mockMvc.perform(delete("/games/{gameId}",1L))
		.andExpect(status().isNoContent());

		assertThat(gameRepository.existsById(1L)).isFalse();
	}

}
