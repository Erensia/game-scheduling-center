package com.erensia.gamescheduling.charactertemplate;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * CharacterTemplate 관련 HTTP 엔드포인트. 04-api-spec.md "템플릿 (CharacterTemplate)" 절 참고.
 *
 * 주의: CharacterController와 마찬가지로 경로가 "/games/{gameId}/templates"와
 * "/templates/{templateId}" 둘로 나뉘어 있어서, 클래스 레벨 @RequestMapping을 두지 않고
 * 메서드마다 전체 경로를 직접 적는다.
 */
@RestController
@RequiredArgsConstructor
public class CharacterTemplateController {

	private final CharacterTemplateService characterTemplateService;

	// GET /games/{gameId}/templates
	@GetMapping("/games/{gameId}/templates")
	public ResponseEntity<List<CharacterTemplateResponse>> getTemplates(@PathVariable Long gameId) {
		// TODO: CharacterController.getCharacters()와 동일한 패턴으로 구현하세요.
		//   1) characterTemplateService.getTemplates(gameId) 호출
		//   2) 결과를 for문으로 순회하며 CharacterTemplateResponse.from()으로 변환
		//   3) ResponseEntity.ok(...)로 응답
		return null;
	}

	// POST /games/{gameId}/templates
	@PostMapping("/games/{gameId}/templates")
	public ResponseEntity<CharacterTemplateResponse> createTemplate(
			@PathVariable Long gameId,
			@Valid @RequestBody CharacterTemplateCreateRequest request) {
		// TODO: CharacterController.createCharacter()와 동일한 패턴으로 구현하세요.
		//   1) request에서 name과 items(텍스트 목록)를 꺼낸다
		//      (items는 List<TemplateItemCreateRequest>이므로, service가 요구하는
		//      List<String>으로 변환하는 절차가 필요할 수 있습니다 - for문 사용)
		//   2) characterTemplateService.createTemplate(gameId, name, itemTexts) 호출
		//   3) ResponseEntity.status(HttpStatus.CREATED).body(CharacterTemplateResponse.from(...))로 응답
		return null;
	}

	// DELETE /templates/{templateId}
	@DeleteMapping("/templates/{templateId}")
	public ResponseEntity<Void> deleteTemplate(@PathVariable Long templateId) {
		// TODO: CharacterController.deleteCharacter()와 동일한 패턴으로 구현하세요.
		return null;
	}

}
