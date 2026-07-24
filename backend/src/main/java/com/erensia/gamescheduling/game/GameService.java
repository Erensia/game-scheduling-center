package com.erensia.gamescheduling.game;

import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Game 관련 비즈니스 로직 계층.
 * 컨트롤러는 이 서비스를 호출하고, 이 서비스가 GameRepository를 호출해 DB와 상호작용한다.
 */
@Service
@RequiredArgsConstructor
public class GameService {

	private final GameRepository gameRepository;

	/**
	 * 게임 생성.
	 */
	public Game createGame(String name, Integer resetDay, Integer partySize) {
		Game game = new Game(name, resetDay, partySize);
		Game savedGame = gameRepository.save(game);

		return savedGame;
	}

	/**
	 * 전체 게임 목록 조회 (GET /games).
	 */
	public List<Game> getAllGames() {
		List<Game> games = gameRepository.findAll();
		return games;
	}

	/**
	 * 게임 설정 수정 (PATCH /games/{gameId}).
	 * findById로 조회한 game은 JPA가 관리 중인 상태라, updateSettings로 필드만 바꾸면
	 * 트랜잭션이 끝날 때 자동으로 UPDATE 쿼리가 나간다 (dirty checking) - save() 호출 불필요.
	 */
	public Game updateGame(Long gameId, Integer resetDay, Integer partySize) {
		Optional<Game> selectedGame = gameRepository.findById(gameId);
		if (selectedGame.isEmpty()) {
			throw new ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다.");
		}
		Game game = selectedGame.get();
		game.updateSettings(resetDay, partySize);

		return game;
	}

	/**
	 * 게임 삭제 (DELETE /games/{gameId}).
	 */
	public void deleteGame(Long gameId) {
		if (!gameRepository.existsById(gameId)) {
			throw new ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다.");
		}
		gameRepository.deleteById(gameId);
	}

}
