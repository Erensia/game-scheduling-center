package com.erensia.gamescheduling.weeklycontent;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * WeeklyContent 관련 HTTP 엔드포인트. 04-api-spec.md "주간 컨텐츠 (WeeklyContent)" 절 참고.
 *
 * 주의: CharacterController와 마찬가지로 경로가 "/games/{gameId}/weekly"와
 * "/weekly/{weeklyId}(/toggle)" 둘로 나뉘어 있어서, 클래스 레벨 @RequestMapping을 두지 않고
 * 메서드마다 전체 경로를 직접 적는다.
 */
@RestController
@RequiredArgsConstructor
public class WeeklyContentController {

	private final WeeklyContentService weeklyContentService;

	// GET /games/{gameId}/weekly
	@GetMapping("/games/{gameId}/weekly")
	public ResponseEntity<List<WeeklyContentResponse>> getWeeklyContents(@PathVariable Long gameId) {
		List<WeeklyContent> weeklyContents = weeklyContentService.getWeeklyContents(gameId);
		List<WeeklyContentResponse> responses = new ArrayList<>();

		for (WeeklyContent weeklyContent : weeklyContents) {
			boolean isCompletedThisWeek = weeklyContentService.isCompletedThisWeek(weeklyContent);
			responses.add(WeeklyContentResponse.from(weeklyContent, isCompletedThisWeek));
		}
		return ResponseEntity.ok(responses);
	}

	// POST /games/{gameId}/weekly (name)
	@PostMapping("/games/{gameId}/weekly")
	public ResponseEntity<WeeklyContentResponse> createWeeklyContent(
			@PathVariable Long gameId,
			@Valid @RequestBody WeeklyContentCreateRequest request) {
		WeeklyContent weeklyContent = weeklyContentService.createWeeklyContent(gameId, request.getName());

		return ResponseEntity.status(HttpStatus.CREATED).body(WeeklyContentResponse.from(weeklyContent, false));
	}

	// PATCH /weekly/{weeklyId}/toggle - 요청 바디 없음
	@PatchMapping("/weekly/{weeklyId}/toggle")
	public ResponseEntity<WeeklyContentResponse> toggleCompletion(@PathVariable Long weeklyId) {
		WeeklyContent weeklyContent = weeklyContentService.toggleCompletion(weeklyId);
		boolean isCompletedThisWeek = weeklyContentService.isCompletedThisWeek(weeklyContent);

		return ResponseEntity.ok(WeeklyContentResponse.from(weeklyContent, isCompletedThisWeek));
	}

	// DELETE /weekly/{weeklyId}
	@DeleteMapping("/weekly/{weeklyId}")
	public ResponseEntity<Void> deleteWeeklyContent(@PathVariable Long weeklyId) {
		weeklyContentService.deleteWeeklyContent(weeklyId);

		return ResponseEntity.noContent().build();
	}

}
