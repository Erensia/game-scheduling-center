package com.erensia.gamescheduling.character;

import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import com.erensia.gamescheduling.game.Game;
import com.erensia.gamescheduling.game.GameRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Character 관련 비즈니스 로직 계층.
 * GameService와 동일한 패턴: 컨트롤러는 이 서비스만 호출하고, 이 서비스가
 * CharacterRepository/GameRepository를 호출해 DB와 상호작용한다.
 */
@Service
@RequiredArgsConstructor
public class CharacterService {

	private final CharacterRepository characterRepository;
	private final GameRepository gameRepository;

	/**
	 * 게임별 캐릭터 목록 조회 (GET /games/{gameId}/characters).
	 * completed가 null이면 전체 목록, 아니면 완료 여부로 필터링한다.
	 */
	public List<Character> getCharacters(Long gameId, Boolean completed) {
		if (!gameRepository.existsById(gameId)) {
			throw new ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다.");
		}
		if (completed == null) {
			return characterRepository.findByGameIdOrderByIdAsc(gameId);
		} else {
			return characterRepository.findByGameIdAndCompletedOrderByIdAsc(gameId, completed);
		}
	}

	/**
	 * 캐릭터 생성 (POST /games/{gameId}/characters).
	 * templateId는 CharacterTemplate 도메인이 구현되기 전까지는 다루지 않는다.
	 */
	public Character createCharacter(Long gameId, String name) {
		Optional<Game> selectedGame = gameRepository.findById(gameId);
		if (selectedGame.isEmpty()) {
			throw new ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다.");
		}
		Game game = selectedGame.get();
		Character character = new Character(game, name);

		return characterRepository.save(character);
	}

	/**
	 * completed 토글 (PATCH /characters/{characterId}).
	 * dirty checking으로 트랜잭션 커밋 시 자동 반영되므로 별도 save() 호출이 필요 없다.
	 */
	@Transactional
	public Character toggleCompleted(Long characterId) {
		Optional<Character> selectedCharacter = characterRepository.findById(characterId);
		if (selectedCharacter.isEmpty()) {
			throw new ResourceNotFoundException("CHARACTER_NOT_FOUND", "해당 캐릭터를 찾을 수 없습니다.");
		}
		Character character = selectedCharacter.get();
		character.toggleCompleted();

		return character;
	}

	/**
	 * 캐릭터 삭제 (DELETE /characters/{characterId}).
	 */
	public void deleteCharacter(Long characterId) {
		if (!characterRepository.existsById(characterId)) {
			throw new ResourceNotFoundException("CHARACTER_NOT_FOUND", "해당 캐릭터를 찾을 수 없습니다.");
		}
		characterRepository.deleteById(characterId);
	}

}
