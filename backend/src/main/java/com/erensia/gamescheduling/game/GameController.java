package com.erensia.gamescheduling.game;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Game 관련 HTTP 엔드포인트. 04-api-spec.md "게임 (Game)" 절 참고.
 *
 * 주의: application.yml에 server.servlet.context-path=/api/v1이 이미 설정돼 있음.
 * 여기서는 "/api/v1"을 다시 붙이지 않는다 (그러면 경로가 중복됨).
 */
@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

	private final GameService gameService;

	// GET /games
	@GetMapping
	public ResponseEntity<List<GameResponse>> getGames() {
		List<Game> games = gameService.getAllGames();
		List<GameResponse> responses = new ArrayList<>();

		for (Game game : games) {
			responses.add(GameResponse.from(game));
		}
		return ResponseEntity.ok(responses);
	}

	// POST /games (name, resetDay, partySize)
	@PostMapping
	public ResponseEntity<GameResponse> createGame(@Valid @RequestBody GameCreateRequest request) {
		String name = request.getName();
		Integer resetDay = request.getResetDay();
		Integer partySize = request.getPartySize();

		Game game = gameService.createGame(name, resetDay, partySize);

		return ResponseEntity.status(HttpStatus.CREATED).body(GameResponse.from(game));
	}

	// PATCH /games/{gameId} - resetDay, partySize만 (name 수정은 범위 밖, 04-api-spec.md)
	@PatchMapping("/{gameId}")
	public ResponseEntity<GameResponse> updateGame(
			@PathVariable Long gameId,
			@Valid @RequestBody GameUpdateRequest request) {
		Integer resetDay = request.getResetDay();
		Integer partySize = request.getPartySize();

		Game game = gameService.updateGame(gameId, resetDay, partySize);

		return ResponseEntity.ok(GameResponse.from(game));
	}

	// DELETE /games/{gameId}
	@DeleteMapping("/{gameId}")
	public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
		gameService.deleteGame(gameId);

		return ResponseEntity.noContent().build();
	}

}
