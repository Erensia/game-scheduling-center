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
 *
 * 진행 방법 (TODO 채우는 순서 추천):
 *  1. GET /games 성공 케이스부터 - MockMvc 기본 패턴(perform -> andExpect) 익히기
 *  2. POST /games 성공 케이스 - JSON 요청 바디 보내는 법 익히기 (ObjectMapper.writeValueAsString)
 *  3. POST /games 검증 실패 케이스 - @Valid 동작 확인 (400 VALIDATION_ERROR)
 *  4. PATCH/DELETE의 성공 케이스와 GAME_NOT_FOUND(404) 케이스
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
		// TODO 1. gameService.getAllGames()가 리턴할 List<Game> 준비 (new Game(...) 1~2개)
		//         주의: Game은 @NoArgsConstructor라 new Game("명조", 3, 3)처럼 생성자로 만들면 id는 null이다.
		//         컨트롤러가 GameResponse.from(game)으로 변환하므로 id 검증까지는 하지 않아도 무방.
		// TODO 2. when(gameService.getAllGames()).thenReturn(준비한_리스트)
		// TODO 3. mockMvc.perform(get("/games"))
		//              .andExpect(status().isOk())
		//              .andExpect(jsonPath("$", hasSize(준비한_리스트_크기)));
		List<Game> games = List.of(new Game("WW", 1, 3), new Game("ZZZ", 1, 3));
		when(gameService.getAllGames()).thenReturn(games);
		mockMvc.perform(get("/games"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void createGame_유효한_요청이면_201과_생성된_게임을_반환한다() throws Exception {
		// TODO 1. GameCreateRequest 객체 준비 (name="명조", resetDay=3, partySize=3)
		// TODO 2. gameService.createGame(...)이 리턴할 Game 준비 및 스텁
		//         (when(gameService.createGame(anyString(), anyInt(), anyInt())).thenReturn(...))
		// TODO 3. mockMvc.perform(post("/games")
		//              .contentType(MediaType.APPLICATION_JSON)
		//              .content(objectMapper.writeValueAsString(요청객체)))
		//              .andExpect(status().isCreated())
		//              .andExpect(jsonPath("$.name").value("명조"));
		GameCreateRequest request = new GameCreateRequest();
		
		request.setName("ww");
		request.setResetDay(1);
		request.setPartySize(3);
		
		Game game = new Game("ww",1,3);
		
		when(gameService.createGame(anyString(),anyInt(),anyInt())).thenReturn(game);
		
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
		// TODO 1. name을 비워둔(또는 resetDay를 범위 밖 값으로 준) GameCreateRequest 준비
		// TODO 2. mockMvc.perform(post("/games")...) 호출
		// TODO 3. status().isBadRequest() 및 jsonPath("$.code").value("VALIDATION_ERROR") 검증
		//         (이 케이스는 gameService를 스텁할 필요가 없다 - @Valid 단계에서 컨트롤러까지 못 들어가고 막히기 때문)
		GameCreateRequest request = new GameCreateRequest();
		request.setName("ww");
		request.setResetDay(10);
		request.setPartySize(3);
		mockMvc.perform(post("/games").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}

	@Test
	void updateGame_존재하는_게임이면_200과_수정된_게임을_반환한다() throws Exception {
		// TODO 1. GameUpdateRequest 준비, gameService.updateGame(anyLong(), anyInt(), anyInt())가
		//         수정된 Game을 리턴하도록 스텁
		// TODO 2. mockMvc.perform(patch("/games/{gameId}", 1L)...) 호출 및 200 검증
		GameUpdateRequest request = new GameUpdateRequest();
		request.setResetDay(3);
		request.setPartySize(4);
		Game game = new Game("ww",3,4);
		when(gameService.updateGame(anyLong(), anyInt(), anyInt())).thenReturn(game);
		mockMvc.perform(patch("/games/{gameId}",1L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(jsonPath("$.resetDay").value(request.getResetDay()))
				.andExpect(jsonPath("$.partySize").value(request.getPartySize()));
		}

	@Test
	void updateGame_존재하지_않는_게임이면_404_GAME_NOT_FOUND를_반환한다() throws Exception {
		// TODO 1. gameService.updateGame(anyLong(), anyInt(), anyInt())가
		//         ResourceNotFoundException("GAME_NOT_FOUND", ...)을 던지도록 스텁
		//         (when(...).thenThrow(new ResourceNotFoundException(...)))
		// TODO 2. mockMvc.perform(patch(...))로 호출 후 status().isNotFound(),
		//         jsonPath("$.code").value("GAME_NOT_FOUND") 검증
		//         (이 동작은 GlobalExceptionHandler가 처리하므로, @WebMvcTest에 자동으로 포함되는지
		//          먼저 확인 - 안 되면 @Import(GlobalExceptionHandler.class) 추가 필요)
		ResourceNotFoundException exception 
			= new ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다."); 
		when(gameService.updateGame(anyLong(), anyInt(), anyInt())).thenThrow(exception);
		
	}

	@Test
	void deleteGame_존재하는_게임이면_204를_반환한다() throws Exception {
		// TODO 1. gameService.deleteGame(anyLong())은 void이므로 기본적으로 아무 것도 안 하면 성공 케이스가 됨
		//         (별도 스텁 없이 mockMvc.perform(delete("/games/{gameId}", 1L))
		//          .andExpect(status().isNoContent()) 만으로 충분)
	}

}
