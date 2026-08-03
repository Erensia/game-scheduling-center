package com.erensia.gamescheduling.character;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import com.erensia.gamescheduling.game.Game;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * CharacterController 슬라이스(slice) 테스트 (스켈레톤).
 *
 * GameControllerTest와 동일한 패턴: @WebMvcTest(CharacterController.class)로 웹 계층만 띄우고
 * CharacterService는 @MockitoBean으로 대체한다. "HTTP 요청 -> 컨트롤러 -> 응답"만 검증하며,
 * 실제 비즈니스 로직은 CharacterServiceTest에서 이미 검증했다고 가정한다.
 *
 * 참고: 헬퍼로 쓸 Game/Character 인스턴스가 필요하면 new Game(...), new Character(game, name)를
 * 직접 생성해서 사용 (Game/Character 둘 다 public 생성자 있음).
 */
@WebMvcTest(CharacterController.class)
class CharacterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CharacterService characterService;

	@Test
	void getCharacters_completed_파라미터_없으면_전체_목록을_200으로_반환한다() throws Exception {
		Game game = new Game();
		List<Character> characters = List.of(new Character(game, "제인 도"), new Character(game, "레미엘 덴"));
		when(characterService.getCharacters(anyLong(), isNull())).thenReturn(characters);
		
		mockMvc.perform(get("/games/{gameId}/characters", 1L))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void getCharacters_completed_쿼리파라미터를_서비스로_그대로_전달한다() throws Exception {
		Game game = new Game();
		List<Character> characters = List.of(new Character(game, "제인 도"));
		when(characterService.getCharacters(anyLong(), eq(true))).thenReturn(characters);
		mockMvc.perform(get("/games/{gameId}/characters?completed=true",1L))
		.andExpect(status().isOk());
		verify(characterService).getCharacters(eq(1L), eq(true));
	}

	@Test
	void createCharacter_유효한_요청이면_201과_생성된_캐릭터를_반환한다() throws Exception {
		CharacterCreateRequest request = new CharacterCreateRequest();
		request.setName("제인 도");
		
		Game game = new Game();
		Character character = new Character(game, "제인 도");
		
		when(characterService.createCharacter(anyLong(), anyString())).thenReturn(character);
		mockMvc.perform(post("/games/{gameId}/characters", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.name").value("제인 도"));
	}

	@Test
	void createCharacter_name이_비어있으면_400_VALIDATION_ERROR를_반환한다() throws Exception {
		CharacterCreateRequest request = new CharacterCreateRequest();
		request.setName(null);
		mockMvc.perform(post("/games/{gameId}/characters", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}

	@Test
	void createCharacter_게임이_존재하지_않으면_404_GAME_NOT_FOUND를_반환한다() throws Exception {
		CharacterCreateRequest request = new CharacterCreateRequest();
		request.setName("제인 도");
		ResourceNotFoundException exception =
				new ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다");
		when(characterService.createCharacter(anyLong(), anyString())).thenThrow(exception);
		mockMvc.perform(post("/games/{gameId}/characters", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value(exception.getCode()));
	}

	@Test
	void toggleCompleted_존재하는_캐릭터면_200과_토글된_결과를_반환한다() throws Exception {
		Game game = new Game();
		Character character = new Character(game, "제인 도");
		character.toggleCompleted();

		when(characterService.toggleCompleted(anyLong())).thenReturn(character);
		mockMvc.perform(patch("/characters/{characterId}", 1L))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.completed").value(character.isCompleted()));
	}

	@Test
	void toggleCompleted_존재하지_않으면_404_CHARACTER_NOT_FOUND를_반환한다() throws Exception {
		ResourceNotFoundException exception =
				new ResourceNotFoundException("CHARACTER_NOT_FOUND", "해당 캐릭터를 찾을 수 없습니다.");
		when(characterService.toggleCompleted(anyLong())).thenThrow(exception);
		mockMvc.perform(patch("/characters/{characterId}", 1L))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value(exception.getCode()));
	}

	@Test
	void deleteCharacter_존재하는_캐릭터면_204를_반환한다() throws Exception {
		mockMvc.perform(delete("/characters/{characterId}", 1L))
		.andExpect(status().isNoContent());
	}

	@Test
	void deleteCharacter_존재하지_않으면_404_CHARACTER_NOT_FOUND를_반환한다() throws Exception {
		ResourceNotFoundException exception =
				new ResourceNotFoundException("CHARACTER_NOT_FOUND", "해당 캐릭터를 찾을 수 없습니다.");
		doThrow(exception).when(characterService).deleteCharacter(anyLong());
		mockMvc.perform(delete("/characters/{characterId}", 1L))
		.andExpect(status().isNotFound());
	}

}
