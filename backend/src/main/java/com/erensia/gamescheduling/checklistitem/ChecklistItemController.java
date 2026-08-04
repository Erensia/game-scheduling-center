package com.erensia.gamescheduling.checklistitem;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * ChecklistItem 관련 HTTP 엔드포인트. 04-api-spec.md "체크리스트 항목 (ChecklistItem)" 절 참고.
 *
 * 주의: CharacterController와 마찬가지로 경로가 "/characters/{characterId}/items"와
 * "/items/{itemId}" 둘로 나뉘어 있어서, 클래스 레벨 @RequestMapping을 두지 않고
 * 메서드마다 전체 경로를 직접 적는다.
 */
@RestController
@RequiredArgsConstructor
public class ChecklistItemController {

	private final ChecklistItemService checklistItemService;

	@PostMapping("/characters/{characterId}/items")
	public ResponseEntity<ChecklistItemResponse> createItem(
			@PathVariable Long characterId,
			@Valid @RequestBody ChecklistItemCreateRequest request) {
		String text = request.getText();
		ChecklistItem checklistItem = checklistItemService.createItem(characterId, text);

		return ResponseEntity.status(HttpStatus.CREATED).body(ChecklistItemResponse.from(checklistItem));
	}

	@PatchMapping("/items/{itemId}")
	public ResponseEntity<ChecklistItemResponse> toggleDone(@PathVariable Long itemId) {
		ChecklistItem checklistItem = checklistItemService.toggleDone(itemId);

		return ResponseEntity.ok(ChecklistItemResponse.from(checklistItem));
	}

	@DeleteMapping("/items/{itemId}")
	public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
		checklistItemService.deleteItem(itemId);

		return ResponseEntity.noContent().build();
	}

}
