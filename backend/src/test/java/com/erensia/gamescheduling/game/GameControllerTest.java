package com.erensia.gamescheduling.game;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * GameController 슬라이스(slice) 테스트.
 *
 * 목적: 실제 DB나 GameService 로직 없이, "HTTP 요청 -> 컨트롤러 -> 응답" 계층만 검증한다.
 * @WebMvcTest(GameController.class)는 웹 계층 관련 빈만 띄우고 GameService는 진짜 빈이 아니라
 * @MockitoBean으로 가짜(mock)로 대체된다 - 그래서 GameServiceTest와 달리 여기서는
 * "GameService가 이렇게 응답한다고 치고, 컨트롤러가 올바른 HTTP status/JSON을 만드는가"만 본다.
 */
@WebMvcTest(GameController.class)
class GameControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private GameService gameService;

	@Test
	void getGames_전체_게임_목록을_200으로_반환한다() throws Exception {
		List<Game> games = List.of(new Game("WW", 1, 3), new Game("ZZZ", 1, 3));

		when(gameService.getAllGames()).thenReturn(games);

		mockMvc.perform(get("/games"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void createGame_유효한_요청이면_201과_생성된_게임을_반환한다() throws Exception {
		GameCreateRequest request = new GameCreateRequest();

		request.setName("ww");
		request.setResetDay(1);
		request.setPartySize(3);

		Game game = new Game("ww", 1, 3);

		when(gameService.createGame(anyString(), anyInt(), anyInt())).thenReturn(game);

		mockMvc.perform(post("/games").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("ww"))
				.andExpect(jsonPath("$.resetDay").value(1))
				.andExpect(jsonPath("$.partySize").value(3));

		verify(gameService).createGame(eq("ww"), eq(1), eq(3));
	}

	@Test
	void createGame_필수값이_비어있으면_400_VALIDATION_ERROR를_반환한다() throws Exception {
		GameCreateRequest request = new GameCreateRequest();

		request.setName("ww");
		request.setResetDay(10);
		request.setPartySize(3);

		mockMvc.perform(post("/games").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}

	@Test
	void updateGame_존재하는_게임이면_200과_수정된_게임을_반환한다() throws Exception {
		GameUpdateRequest request = new GameUpdateRequest();

		request.setResetDay(3);
		request.setPartySize(4);

		Game game = new Game("ww", 3, 4);

		when(gameService.updateGame(anyLong(), anyInt(), anyInt())).thenReturn(game);

		mockMvc.perform(patch("/games/{gameId}", 1L).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.resetDay").value(request.getResetDay()))
				.andExpect(jsonPath("$.partySize").value(request.getPartySize()));
	}

	@Test
	void updateGame_존재하지_않는_게임이면_404_GAME_NOT_FOUND를_반환한다() throws Exception {
		GameUpdateRequest request = new GameUpdateRequest();

		request.setResetDay(3);
		request.setPartySize(4);

		ResourceNotFoundException exception
			= new ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다.");

		when(gameService.updateGame(anyLong(), anyInt(), anyInt())).thenThrow(exception);

		mockMvc.perform(patch("/games/{gameId}", 1L).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("GAME_NOT_FOUND"));
	}

	@Test
	void deleteGame_존재하는_게임이면_204를_반환한다() throws Exception {
		mockMvc.perform(delete("/games/{gameId}", 1L))
				.andExpect(status().isNoContent());
	}

}
