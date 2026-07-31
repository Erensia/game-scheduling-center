package com.erensia.gamescheduling.character;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
 *
 * TODO: 아래 각 테스트 메서드에 given(mockitoBean 스텁) - when(mockMvc.perform) - then(expect) 채우기.
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
		// TODO: characterService.getCharacters(anyLong(), isNull()) -> List.of(캐릭터 2개) 스텁
		// TODO: mockMvc.perform(get("/games/{gameId}/characters", 1L)) 로 호출
		// TODO: status().isOk(), jsonPath("$", hasSize(2)) 검증
	}

	@Test
	void getCharacters_completed_쿼리파라미터를_서비스로_그대로_전달한다() throws Exception {
		// TODO: characterService.getCharacters(anyLong(), eq(true)) 스텁
		// TODO: mockMvc.perform(get("/games/{gameId}/characters?completed=true", 1L)) 호출
		// TODO: verify(characterService).getCharacters(eq(1L), eq(true)) 로 파라미터 전달 검증
	}

	@Test
	void createCharacter_유효한_요청이면_201과_생성된_캐릭터를_반환한다() throws Exception {
		// TODO: CharacterCreateRequest 준비 (name 설정)
		// TODO: characterService.createCharacter(anyLong(), anyString()) -> Character 스텁
		// TODO: mockMvc.perform(post("/games/{gameId}/characters", 1L)...) 호출
		// TODO: status().isCreated(), jsonPath("$.name") 등 검증
	}

	@Test
	void createCharacter_name이_비어있으면_400_VALIDATION_ERROR를_반환한다() throws Exception {
		// TODO: CharacterCreateRequest의 name을 비워두거나 null로 설정 (GameControllerTest의 검증 실패 케이스 참고)
		// TODO: mockMvc.perform(post(...)) 호출 후 status().isBadRequest(), jsonPath("$.code").value("VALIDATION_ERROR") 검증
	}

	@Test
	void createCharacter_게임이_존재하지_않으면_404_GAME_NOT_FOUND를_반환한다() throws Exception {
		// TODO: characterService.createCharacter(...)가 ResourceNotFoundException("GAME_NOT_FOUND", ...) 던지도록 스텁
		// TODO: mockMvc.perform(post(...)) 호출 후 status().isNotFound(), jsonPath("$.code").value("GAME_NOT_FOUND") 검증
	}

	@Test
	void toggleCompleted_존재하는_캐릭터면_200과_토글된_결과를_반환한다() throws Exception {
		// TODO: characterService.toggleCompleted(anyLong()) -> completed가 반전된 Character 스텁
		// TODO: mockMvc.perform(patch("/characters/{characterId}", 1L)) 호출
		// TODO: status().isOk(), jsonPath("$.completed") 검증
	}

	@Test
	void toggleCompleted_존재하지_않으면_404_CHARACTER_NOT_FOUND를_반환한다() throws Exception {
		// TODO: characterService.toggleCompleted(anyLong())가 ResourceNotFoundException("CHARACTER_NOT_FOUND", ...) 던지도록 스텁
		// TODO: mockMvc.perform(patch(...)) 호출 후 status().isNotFound(), jsonPath("$.code").value("CHARACTER_NOT_FOUND") 검증
	}

	@Test
	void deleteCharacter_존재하는_캐릭터면_204를_반환한다() throws Exception {
		// TODO: mockMvc.perform(delete("/characters/{characterId}", 1L)) 호출 후 status().isNoContent() 검증
	}

	@Test
	void deleteCharacter_존재하지_않으면_404_CHARACTER_NOT_FOUND를_반환한다() throws Exception {
		// TODO: characterService.deleteCharacter(anyLong())가 ResourceNotFoundException 던지도록 스텁
		// TODO: mockMvc.perform(delete(...)) 호출 후 status().isNotFound() 검증
	}

}
