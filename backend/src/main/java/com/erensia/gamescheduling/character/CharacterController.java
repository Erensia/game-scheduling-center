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

	// TODO: GET /games/{gameId}/characters
	//  - @GetMapping("/games/{gameId}/characters")
	//  - public ResponseEntity<List<CharacterResponse>> getCharacters(
	//        @PathVariable Long gameId,
	//        @RequestParam(required = false) Boolean completed) { ... }
	//  - GameController.getGames()처럼 List<Character>를 받아서 for문으로 CharacterResponse.from(...) 변환
	//    (스트림/람다 금지 - for문 사용)

	// TODO: POST /games/{gameId}/characters
	//  - @PostMapping("/games/{gameId}/characters")
	//  - public ResponseEntity<CharacterResponse> createCharacter(
	//        @PathVariable Long gameId,
	//        @Valid @RequestBody CharacterCreateRequest request) { ... }
	//  - GameController.createGame()처럼 request에서 필드 꺼내서 서비스에 전달
	//  - ResponseEntity.status(HttpStatus.CREATED).body(CharacterResponse.from(character))

	// TODO: PATCH /characters/{characterId}
	//  - @PatchMapping("/characters/{characterId}")
	//  - public ResponseEntity<CharacterResponse> toggleCompleted(@PathVariable Long characterId) { ... }
	//  - 04-api-spec.md: "completed 토글 - 이름 수정은 MVP 범위에서 제외"
	//  - 요청 바디 없음에 주의 (@RequestBody 받지 않음, GameController의 PATCH와 다른 부분)

	// TODO: DELETE /characters/{characterId}
	//  - @DeleteMapping("/characters/{characterId}")
	//  - public ResponseEntity<Void> deleteCharacter(@PathVariable Long characterId) { ... }
	//  - GameController.deleteGame()과 동일: ResponseEntity.noContent().build()

}
