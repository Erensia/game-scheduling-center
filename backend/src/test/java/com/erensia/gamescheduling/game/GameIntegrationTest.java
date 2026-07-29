package com.erensia.gamescheduling.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
 *
 * 진행 방법 (TODO 채우는 순서 추천):
 *  1. createGame_후_목록조회 하나만 먼저 끝까지 통과시켜서 Testcontainers가 실제로 뜨는지 확인
 *  2. 나머지 시나리오(수정/삭제/404)를 채워나가기 - GameControllerTest와 구조는 비슷하되
 *     mock 스텁이 없다는 점, 그리고 "생성 -> 그 응답의 id로 조회/수정/삭제"처럼
 *     여러 단계를 이어붙여 검증할 수 있다는 점이 다르다.
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
		// TODO: 각 테스트가 서로 영향을 주지 않도록, 테스트 시작 전 games 테이블을 비운다.
		// gameRepository.deleteAll() 정도면 충분 (하드 삭제로 통일하기로 한 결정과도 맞음, 01-requirements.md 6절)
	}

	@Test
	void 게임을_생성하면_목록_조회에서_확인할_수_있다() throws Exception {
		// TODO 1. GameCreateRequest("명조", 3, 3) 같은 요청 바디 준비
		// TODO 2. mockMvc.perform(post("/games")...) 로 실제 생성 요청 -> status().isCreated() 검증
		// TODO 3. mockMvc.perform(get("/games")) 로 목록 조회 -> 방금 만든 게임이 포함돼 있는지 검증
		//         (jsonPath 대신, gameRepository.findAll()로 직접 DB 상태를 확인해도 된다 -
		//          "API로 만든 데이터가 실제 DB에 반영됐는가"를 보는 것도 통합 테스트의 목적 중 하나)
	}

	@Test
	void 존재하지_않는_게임을_수정하면_404_GAME_NOT_FOUND를_반환한다() throws Exception {
		// TODO 1. gameRepository가 비어있는 상태에서(= @BeforeEach cleanUp 직후) 임의의 id로 PATCH 요청
		// TODO 2. status().isNotFound(), jsonPath("$.code").value("GAME_NOT_FOUND") 검증
	}

	@Test
	void 게임을_삭제하면_DB에서도_실제로_사라진다() throws Exception {
		// TODO 1. gameRepository.save(new Game(...))로 미리 게임 하나를 DB에 직접 저장
		// TODO 2. mockMvc.perform(delete("/games/{gameId}", 저장된_id)) -> status().isNoContent() 검증
		// TODO 3. assertThat(gameRepository.existsById(저장된_id)).isFalse() 로 실제 삭제 확인
	}

}
