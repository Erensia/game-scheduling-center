package com.erensia.gamescheduling.checklistitem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erensia.gamescheduling.character.Character;
import com.erensia.gamescheduling.character.CharacterRepository;
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
 * ChecklistItem 도메인 통합 테스트.
 *
 * CharacterIntegrationTest와 동일한 패턴: Controller -> Service -> Repository -> 실제
 * PostgreSQL(Testcontainers)까지 전부 진짜로 붙여서 검증한다.
 *
 * 주의: ChecklistItem 전용 GET 엔드포인트가 없다 (04-api-spec.md 참고). "생성/토글이 실제로
 * 반영됐는지"는 GET /games/{gameId}/characters 응답의 items 필드로 확인하거나,
 * checklistItemRepository로 직접 조회해서 검증한다.
 *
 * 주의: ChecklistItem은 Character에 종속되고(character_id nullable=false), Character는
 * Game에 종속된다. FK 순서상 cleanUp에서 자식(checklistItem)부터 지운다.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ChecklistItemIntegrationTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ChecklistItemRepository checklistItemRepository;

	@Autowired
	private CharacterRepository characterRepository;

	@Autowired
	private GameRepository gameRepository;

	@BeforeEach
	void cleanUp() {
		// FK 순서 주의 - checklistItem -> character -> game 순으로 삭제.
		checklistItemRepository.deleteAll();
		characterRepository.deleteAll();
		gameRepository.deleteAll();
	}

	@Test
	void 항목을_생성하면_캐릭터_조회_응답에_포함된다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);

		Character character = characterRepository.save(new Character(game, "장리"));

		ChecklistItemCreateRequest request = new ChecklistItemCreateRequest();
		request.setText("text");

		mockMvc.perform(post("/characters/{characterId}/items", character.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.text").value(request.getText()));

		mockMvc.perform(get("/games/{gameId}/characters", game.getId()))
		.andExpect(jsonPath("$[0].items[0].text").value(request.getText()));
	}

	@Test
	void 존재하지_않는_캐릭터에_항목을_생성하면_404_CHARACTER_NOT_FOUND를_반환한다() throws Exception {
		ChecklistItemCreateRequest request = new ChecklistItemCreateRequest();
		request.setText("text");

		mockMvc.perform(post("/characters/{characterId}/items", Long.MAX_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("CHARACTER_NOT_FOUND"));
	}

	@Test
	void 항목의_done을_토글할_수_있다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);

		Character character = characterRepository.save(new Character(game, "장리"));
		ChecklistItem item = checklistItemRepository.save(new ChecklistItem(character, "text"));

		mockMvc.perform(patch("/items/{itemId}", item.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.done").value(true));

		mockMvc.perform(patch("/items/{itemId}", item.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.done").value(false));
	}

	@Test
	void 존재하지_않는_항목을_토글하면_404_ITEM_NOT_FOUND를_반환한다() throws Exception {
		mockMvc.perform(patch("/items/{itemId}", Long.MAX_VALUE))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value("ITEM_NOT_FOUND"));
	}

	@Test
	void 항목을_삭제하면_DB에서도_실제로_사라진다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);

		Character character = characterRepository.save(new Character(game, "장리"));
		ChecklistItem item = checklistItemRepository.save(new ChecklistItem(character, "text"));

		mockMvc.perform(delete("/items/{itemId}", item.getId()))
		.andExpect(status().isNoContent());

		assertThat(checklistItemRepository.existsById(item.getId())).isFalse();
	}

	@Test
	void 캐릭터를_삭제하면_소속된_체크리스트_항목도_함께_연쇄_삭제된다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		gameRepository.save(game);

		Character character = characterRepository.save(new Character(game, "장리"));

		ChecklistItem item = checklistItemRepository.save(new ChecklistItem(character, "text"));
		checklistItemRepository.save(new ChecklistItem(character, "text2"));
		checklistItemRepository.save(new ChecklistItem(character, "text3"));

		mockMvc.perform(delete("/characters/{characterId}", character.getId()))
		.andExpect(status().isNoContent());

		assertThat(characterRepository.existsById(character.getId())).isFalse();
		assertThat(checklistItemRepository.existsById(item.getId())).isFalse();
	}

}
