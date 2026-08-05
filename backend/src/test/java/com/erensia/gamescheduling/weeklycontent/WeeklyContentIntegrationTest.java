package com.erensia.gamescheduling.weeklycontent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erensia.gamescheduling.game.Game;
import com.erensia.gamescheduling.game.GameRepository;
import java.time.LocalDate;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * WeeklyContent 도메인 통합 테스트.
 *
 * CharacterIntegrationTest와 동일한 패턴: Controller -> Service -> Repository -> 실제
 * PostgreSQL(Testcontainers)까지 전부 진짜로 붙여서 검증한다.
 *
 * - WeeklyContent는 Game에 종속되므로(game_id nullable=false), 각 테스트마다 먼저
 *   gameRepository.save(new Game(...))로 부모 Game을 저장한 뒤 그 game의 id를 사용한다.
 * - 토글 테스트는 completedThisWeek가 game.resetDay와 오늘 날짜로 계산되는 점을 이용해,
 *   resetDay를 "오늘의 요일"로 맞춰 실행일과 무관하게 항상 성립하도록 만들었다
 *   (calculateWeekStart 공식 자체의 정확성은 WeeklyContentServiceTest에서 이미 검증했다).
 * - Game 삭제 시 소속 WeeklyContent도 cascade 삭제되는지(Game.java의 weeklyContents 필드,
 *   cascade = CascadeType.REMOVE, orphanRemoval = true) 확인하는 케이스도 포함한다.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class WeeklyContentIntegrationTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WeeklyContentRepository weeklyContentRepository;

	@Autowired
	private GameRepository gameRepository;

	@BeforeEach
	void cleanUp() {
		// FK 순서 주의 - WeeklyContent가 Game을 참조하므로 자식부터 지운다.
		weeklyContentRepository.deleteAll();
		gameRepository.deleteAll();
	}

	@Test
	void 주간컨텐츠를_생성하면_목록_조회에서_확인할_수_있다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);

		WeeklyContentCreateRequest request = new WeeklyContentCreateRequest();
		request.setName("content1");

		mockMvc.perform(post("/games/{gameId}/weekly", game.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value(request.getName()));

		mockMvc.perform(get("/games/{gameId}/weekly", game.getId()))
				.andExpect(jsonPath("$[0].name").value(request.getName()));
	}

	@Test
	void 존재하지_않는_게임에_주간컨텐츠를_생성하면_404_GAME_NOT_FOUND를_반환한다() throws Exception {
		WeeklyContentCreateRequest request = new WeeklyContentCreateRequest();
		request.setName("content1");

		mockMvc.perform(post("/games/{gameId}/weekly", Long.MAX_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("GAME_NOT_FOUND"));
	}

	@Test
	void 주간컨텐츠의_완료_상태를_토글할_수_있다() throws Exception {
		Game game = new Game("wuwa", null, 3);
		int resetDay = (LocalDate.now().getDayOfWeek().getValue()) % 7;
		game.updateSettings(resetDay, 3);
		gameRepository.save(game);

		WeeklyContent weeklyContent = new WeeklyContent(game, "content1");
		weeklyContentRepository.save(weeklyContent);

		mockMvc.perform(patch("/weekly/{weeklyId}/toggle", weeklyContent.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.completedThisWeek").value(true));

		mockMvc.perform(patch("/weekly/{weeklyId}/toggle", weeklyContent.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.completedThisWeek").value(false));
	}

	@Test
	void 주간컨텐츠를_삭제하면_DB에서도_실제로_사라진다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);
		WeeklyContent weeklyContent = weeklyContentRepository.save(new WeeklyContent(game, "content1"));

		mockMvc.perform(delete("/weekly/{weeklyId}", weeklyContent.getId()))
				.andExpect(status().isNoContent());

		assertThat(weeklyContentRepository.existsById(weeklyContent.getId())).isFalse();
	}

	@Test
	void 게임을_삭제하면_소속된_주간컨텐츠도_함께_연쇄_삭제된다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);

		WeeklyContent weeklyContent = weeklyContentRepository.save(new WeeklyContent(game, "content1"));
		weeklyContentRepository.save(new WeeklyContent(game, "content2"));
		weeklyContentRepository.save(new WeeklyContent(game, "content3"));

		mockMvc.perform(delete("/games/{gameId}", game.getId()))
				.andExpect(status().isNoContent());

		assertThat(gameRepository.existsById(game.getId())).isFalse();
		assertThat(weeklyContentRepository.existsById(weeklyContent.getId())).isFalse();
	}

}
