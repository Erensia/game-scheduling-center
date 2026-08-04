package com.erensia.gamescheduling.checklistitem;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erensia.gamescheduling.character.Character;
import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import com.erensia.gamescheduling.game.Game;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * ChecklistItemController 슬라이스(slice) 테스트 (스켈레톤).
 *
 * CharacterControllerTest와 동일한 패턴: @WebMvcTest(ChecklistItemController.class)로 웹
 * 계층만 띄우고 ChecklistItemService는 @MockitoBean으로 대체한다. 비즈니스 로직은
 * ChecklistItemServiceTest에서 이미 검증했다고 가정하고, 여기서는 "HTTP 요청 -> 컨트롤러 -> 응답"만
 * 검증한다.
 *
 * 참고: 헬퍼로 쓸 Character/ChecklistItem 인스턴스가 필요하면 new Game(...), new Character(game, name),
 * new ChecklistItem(character, text)를 직접 생성해서 사용.
 */
@WebMvcTest(ChecklistItemController.class)
class ChecklistItemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ChecklistItemService checklistItemService;

	@Test
	void createItem_유효한_요청이면_201과_생성된_항목을_반환한다() throws Exception {
		ChecklistItemCreateRequest request = new ChecklistItemCreateRequest();
		request.setText("text");

		Game game = new Game("wuwa", 1, 3);
		Character character = new Character(game, "장리");
		ChecklistItem item = new ChecklistItem(character, "text");

		when(checklistItemService.createItem(anyLong(), anyString())).thenReturn(item);

		mockMvc.perform(post("/characters/{characterId}/items", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.text").value(item.getText()));
	}

	@Test
	void createItem_text가_비어있으면_400_VALIDATION_ERROR를_반환한다() throws Exception {
		ChecklistItemCreateRequest request = new ChecklistItemCreateRequest();
		request.setText(null);

		mockMvc.perform(post("/characters/{characterId}/items", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}

	@Test
	void createItem_캐릭터가_존재하지_않으면_404_CHARACTER_NOT_FOUND를_반환한다() throws Exception {
		ChecklistItemCreateRequest request = new ChecklistItemCreateRequest();
		request.setText("text");

		ResourceNotFoundException exception =
				new ResourceNotFoundException("CHARACTER_NOT_FOUND", "해당 캐릭터를 찾을 수 없습니다.");

		when(checklistItemService.createItem(anyLong(), anyString())).thenThrow(exception);
		mockMvc.perform(post("/characters/{characterId}/items", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value(exception.getCode()));
	}

	@Test
	void toggleDone_존재하는_항목이면_200과_토글된_결과를_반환한다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		Character character = new Character(game, "장리");
		ChecklistItem item = new ChecklistItem(character, "text");
		item.toggleDone();
		when(checklistItemService.toggleDone(anyLong())).thenReturn(item);
		mockMvc.perform(patch("/items/{itemId}", 1L))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.done").value(item.isDone()));
	}

	@Test
	void toggleDone_존재하지_않으면_404_ITEM_NOT_FOUND를_반환한다() throws Exception {
		ResourceNotFoundException exception =
				new ResourceNotFoundException("ITEM_NOT_FOUND", "해당 체크리스트 항목을 찾을 수 없습니다.");
		when(checklistItemService.toggleDone(anyLong())).thenThrow(exception);
		mockMvc.perform(patch("/items/{itemId}", 1L))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value(exception.getCode()));
	}

	@Test
	void deleteItem_존재하는_항목이면_204를_반환한다() throws Exception {
		mockMvc.perform(delete("/items/{itemId}", 1L))
		.andExpect(status().isNoContent());
	}

	@Test
	void deleteItem_존재하지_않으면_404_ITEM_NOT_FOUND를_반환한다() throws Exception {
		ResourceNotFoundException exception =
				new ResourceNotFoundException("ITEM_NOT_FOUND", "해당 체크리스트 항목을 찾을 수 없습니다.");
		doThrow(exception).when(checklistItemService).deleteItem(anyLong());
		mockMvc.perform(delete("/items/{itemId}", 1L))
		.andExpect(status().isNotFound());
	}

}
