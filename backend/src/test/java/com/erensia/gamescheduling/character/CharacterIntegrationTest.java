package com.erensia.gamescheduling.character;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erensia.gamescheduling.game.Game;
import com.erensia.gamescheduling.game.GameRepository;
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
 * Character 도메인 통합 테스트.
 *
 * GameIntegrationTest와 동일한 패턴: Controller -> Service -> Repository -> 실제 PostgreSQL
 * (Testcontainers)까지 전부 진짜로 붙여서 검증한다.
 *
 * 주의: Character는 Game에 종속되므로(game_id nullable=false), 테스트마다 먼저
 * gameRepository.save(new Game(...))로 부모 Game을 만들어 둔 뒤 그 game의 id를 사용해야 한다.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CharacterIntegrationTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CharacterRepository characterRepository;

	@Autowired
	private GameRepository gameRepository;

	@BeforeEach
	void cleanUp() {
		// FK 순서 주의 - Character가 Game을 참조하므로 자식부터 지운다.
		characterRepository.deleteAll();
		gameRepository.deleteAll();
	}

	@Test
	void 캐릭터를_생성하면_목록_조회에서_확인할_수_있다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);

		CharacterCreateRequest request = new CharacterCreateRequest();
		request.setName("장리");

		mockMvc.perform(post("/games/{gameId}/characters", game.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.name").value(request.getName()));

		mockMvc.perform(get("/games/{gameId}/characters", game.getId()))
		.andExpect(jsonPath("$[0].name").value(request.getName()));
	}

	@Test
	void completed_필터로_캐릭터_목록을_조회할_수_있다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);

		Character character = characterRepository.save(new Character(game, "장리"));
		character.toggleCompleted();
		characterRepository.save(character);

		characterRepository.save(new Character(game, "젠니"));

		character = characterRepository.save(new Character(game, "유노"));
		character.toggleCompleted();
		characterRepository.save(character);

		characterRepository.save(new Character(game, "히유키"));

		mockMvc.perform(get("/games/{gameId}/characters?completed=true", game.getId()))
		.andExpect(jsonPath("$.length()").value(2))
		.andExpect(jsonPath("$[0].completed").value(true))
		.andExpect(jsonPath("$[1].completed").value(true));
	}

	@Test
	void 존재하지_않는_게임에_캐릭터를_생성하면_404_GAME_NOT_FOUND를_반환한다() throws Exception {
		CharacterCreateRequest request = new CharacterCreateRequest();
		request.setName("장리");

		mockMvc.perform(post("/games/{gameId}/characters", Long.MAX_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("GAME_NOT_FOUND"));
	}

	@Test
	void 캐릭터의_completed를_토글할_수_있다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);

		Character character = characterRepository.save(new Character(game, "장리"));

		mockMvc.perform(patch("/characters/{characterId}", character.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(character)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.completed").value(true));

		mockMvc.perform(patch("/characters/{characterId}", character.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(character)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.completed").value(false));
	}

	@Test
	void 캐릭터를_삭제하면_DB에서도_실제로_사라진다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);

		Character character = characterRepository.save(new Character(game, "장리"));

		mockMvc.perform(delete("/characters/{characterId}", character.getId()))
		.andExpect(status().isNoContent());

		assertThat(characterRepository.existsById(character.getId())).isFalse();
	}

	@Test
	void 게임을_삭제하면_소속된_캐릭터도_함께_연쇄_삭제된다() throws Exception {
		// 삭제 정책 (2026-07-31 결정): Game 삭제 시 소속 Character 전부 연쇄 삭제.
		// Game.java의 @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true) characters로 처리됨.
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);

		Character character = characterRepository.save(new Character(game, "장리"));
		characterRepository.save(new Character(game, "유노"));
		characterRepository.save(new Character(game, "젠니"));

		mockMvc.perform(delete("/games/{gameId}", game.getId()))
		.andExpect(status().isNoContent());

		assertThat(gameRepository.existsById(game.getId())).isFalse();
		assertThat(characterRepository.existsById(character.getId())).isFalse();
	}

}
