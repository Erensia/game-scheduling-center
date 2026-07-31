package com.erensia.gamescheduling.character;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Character 관련 HTTP 엔드포인트. 04-api-spec.md "캐릭터 (Character)" 절 참고.
 *
 * 주의: Game과 달리 경로가 "/games/{gameId}/characters"와 "/characters/{characterId}"
 * 둘로 나뉘어 있어서, GameController처럼 클래스 레벨 @RequestMapping을 두지 않고
 * 메서드마다 전체 경로를 직접 적는다.
 */
@RestController
@RequiredArgsConstructor
public class CharacterController {

	private final CharacterService characterService;

	// GET /games/{gameId}/characters (필터: ?completed=true/false)
	@GetMapping("/games/{gameId}/characters")
	public ResponseEntity<List<CharacterResponse>> getCharacters(
			@PathVariable Long gameId,
			@RequestParam(required = false) Boolean completed) {
		List<Character> characters = characterService.getCharacters(gameId, completed);
		List<CharacterResponse> responses = new ArrayList<>();

		for (Character character : characters) {
			responses.add(CharacterResponse.from(character));
		}
		return ResponseEntity.ok(responses);
	}

	// POST /games/{gameId}/characters (name, templateId는 이번 사이클에서 미사용)
	@PostMapping("/games/{gameId}/characters")
	public ResponseEntity<CharacterResponse> createCharacter(
			@PathVariable Long gameId,
			@Valid @RequestBody CharacterCreateRequest request) {
		String name = request.getName();
		Character character = characterService.createCharacter(gameId, name);

		return ResponseEntity.status(HttpStatus.CREATED).body(CharacterResponse.from(character));
	}

	// PATCH /characters/{characterId} - completed 토글 (요청 바디 없음)
	@PatchMapping("/characters/{characterId}")
	public ResponseEntity<CharacterResponse> toggleCompleted(@PathVariable Long characterId) {
		Character character = characterService.toggleCompleted(characterId);

		return ResponseEntity.ok(CharacterResponse.from(character));
	}

	// DELETE /characters/{characterId}
	@DeleteMapping("/characters/{characterId}")
	public ResponseEntity<Void> deleteCharacter(@PathVariable Long characterId) {
		characterService.deleteCharacter(characterId);

		return ResponseEntity.noContent().build();
	}

}
