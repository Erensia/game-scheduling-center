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
		// TODO: weeklyContentService.getWeeklyContents(gameId) 호출.
		// TODO: 결과 List<WeeklyContent>를 순회하며 WeeklyContentResponse.from(...)으로 변환해
		//       새 ArrayList에 담고 ResponseEntity.ok(...)로 반환하세요.
		//       GameController.getGames(), CharacterController.getCharacters()와 동일한 패턴입니다 (람다 금지, for문 사용).
		return null;
	}

	// POST /games/{gameId}/weekly (name)
	@PostMapping("/games/{gameId}/weekly")
	public ResponseEntity<WeeklyContentResponse> createWeeklyContent(
			@PathVariable Long gameId,
			@Valid @RequestBody WeeklyContentCreateRequest request) {
		// TODO: request에서 name을 꺼내 weeklyContentService.createWeeklyContent(gameId, name) 호출.
		// TODO: ResponseEntity.status(HttpStatus.CREATED).body(WeeklyContentResponse.from(...))로 반환.
		return null;
	}

	// PATCH /weekly/{weeklyId}/toggle - 요청 바디 없음
	@PatchMapping("/weekly/{weeklyId}/toggle")
	public ResponseEntity<WeeklyContentResponse> toggleCompletion(@PathVariable Long weeklyId) {
		// TODO: weeklyContentService.toggleCompletion(weeklyId) 호출 후 WeeklyContentResponse.from(...)으로 감싸 반환.
		return null;
	}

	// DELETE /weekly/{weeklyId}
	@DeleteMapping("/weekly/{weeklyId}")
	public ResponseEntity<Void> deleteWeeklyContent(@PathVariable Long weeklyId) {
		// TODO: weeklyContentService.deleteWeeklyContent(weeklyId) 호출 후 ResponseEntity.noContent().build() 반환.
		return null;
	}

}
