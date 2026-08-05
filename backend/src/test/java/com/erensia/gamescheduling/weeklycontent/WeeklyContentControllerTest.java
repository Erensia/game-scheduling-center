package com.erensia.gamescheduling.weeklycontent;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
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
 * WeeklyContentController 슬라이스(slice) 테스트.
 *
 * CharacterControllerTest와 동일한 패턴: @WebMvcTest(WeeklyContentController.class)로 웹 계층만
 * 띄우고 WeeklyContentService는 @MockitoBean으로 대체한다. "HTTP 요청 -> 컨트롤러 -> 응답"만
 * 검증하며, 비즈니스 로직(특히 completedThisWeek 계산)은 WeeklyContentServiceTest에서 이미
 * 검증했다는 전제로 필요한 값만 mock으로 고정한다.
 */
@WebMvcTest(WeeklyContentController.class)
class WeeklyContentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private WeeklyContentService weeklyContentService;

	@Test
	void getWeeklyContents_목록을_200과_함께_반환한다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		List<WeeklyContent> weeklyContents = List.of(new WeeklyContent(game, "content1"),
				new WeeklyContent(game, "content2"), new WeeklyContent(game, "content3"));
		when(weeklyContentService.getWeeklyContents(anyLong())).thenReturn(weeklyContents);
		when(weeklyContentService.isCompletedThisWeek(any(WeeklyContent.class))).thenReturn(false);

		mockMvc.perform(get("/games/{gameId}/weekly", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)));
	}

	@Test
	void createWeeklyContent_유효한_요청이면_201과_생성된_컨텐츠를_반환한다() throws Exception {
		WeeklyContentCreateRequest request = new WeeklyContentCreateRequest();
		request.setName("content1");
		Game game = new Game();
		WeeklyContent weeklyContent = new WeeklyContent(game, "content1");
		when(weeklyContentService.createWeeklyContent(anyLong(), anyString())).thenReturn(weeklyContent);

		mockMvc.perform(post("/games/{gameId}/weekly", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value(weeklyContent.getName()));
	}

	@Test
	void createWeeklyContent_name이_비어있으면_400_VALIDATION_ERROR를_반환한다() throws Exception {
		WeeklyContentCreateRequest request = new WeeklyContentCreateRequest();
		request.setName(null);

		mockMvc.perform(post("/games/{gameId}/weekly", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}

	@Test
	void createWeeklyContent_게임이_존재하지_않으면_404_GAME_NOT_FOUND를_반환한다() throws Exception {
		WeeklyContentCreateRequest request = new WeeklyContentCreateRequest();
		request.setName("content1");
		ResourceNotFoundException exception =
				new ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다.");
		when(weeklyContentService.createWeeklyContent(anyLong(), anyString())).thenThrow(exception);

		mockMvc.perform(post("/games/{gameId}/weekly", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(exception.getCode()));
	}

	@Test
	void toggleCompletion_존재하는_컨텐츠면_200과_토글된_completedThisWeek를_반환한다() throws Exception {
		Game game = new Game("wuwa", 1, 3);
		WeeklyContent weeklyContent = new WeeklyContent(game, "content1");
		when(weeklyContentService.toggleCompletion(anyLong())).thenReturn(weeklyContent);
		when(weeklyContentService.isCompletedThisWeek(any(WeeklyContent.class))).thenReturn(false);

		mockMvc.perform(patch("/weekly/{weeklyId}/toggle", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.completedThisWeek").value(false));
	}

	@Test
	void toggleCompletion_존재하지_않으면_404_WEEKLY_CONTENT_NOT_FOUND를_반환한다() throws Exception {
		ResourceNotFoundException exception =
				new ResourceNotFoundException("WEEKLY_CONTENT_NOT_FOUND", "주간 컨텐츠를 찾을 수 없습니다.");
		when(weeklyContentService.toggleCompletion(anyLong())).thenThrow(exception);

		mockMvc.perform(patch("/weekly/{weeklyId}/toggle", 1L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("WEEKLY_CONTENT_NOT_FOUND"));
	}

	@Test
	void deleteWeeklyContent_존재하는_컨텐츠면_204를_반환한다() throws Exception {
		mockMvc.perform(delete("/weekly/{weeklyId}", 1L))
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteWeeklyContent_존재하지_않으면_404_WEEKLY_CONTENT_NOT_FOUND를_반환한다() throws Exception {
		ResourceNotFoundException exception =
				new ResourceNotFoundException("WEEKLY_CONTENT_NOT_FOUND", "주간 컨텐츠를 찾을 수 없습니다.");
		doThrow(exception).when(weeklyContentService).deleteWeeklyContent(anyLong());

		mockMvc.perform(delete("/weekly/{weeklyId}", 1L))
				.andExpect(status().isNotFound());
	}

}
