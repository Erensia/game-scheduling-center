package com.erensia.gamescheduling.character;

import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import com.erensia.gamescheduling.game.Game;
import com.erensia.gamescheduling.game.GameRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

	// TODO: 캐릭터 목록 조회 (GET /games/{gameId}/characters)
	//  - public List<Character> getCharacters(Long gameId, Boolean completed) { ... }
	//  - completed가 null이면 characterRepository.findByGameIdOrderByIdAsc(gameId) 호출
	//  - completed가 null이 아니면 characterRepository.findByGameIdAndCompletedOrderByIdAsc(gameId, completed) 호출
	//  - 존재하지 않는 gameId를 어떻게 처리할지 고민해볼 것
	//    (검증 없이 그냥 빈 목록을 돌려줄지, GAME_NOT_FOUND를 던질지는 04-api-spec.md 기준으로 판단)

	// TODO: 캐릭터 생성 (POST /games/{gameId}/characters)
	//  - public Character createCharacter(Long gameId, String name) { ... }
	//  - GameService.updateGame처럼 Optional<Game> selectedGame = gameRepository.findById(gameId) 로 조회
	//  - 없으면 throw new ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다.");
	//  - new Character(game, name)으로 생성 후 characterRepository.save(...)
	//  - templateId(요청 DTO에는 있음)는 이번 사이클에서 무시 - CharacterTemplate 도메인이 아직 없음
	//    (Controller에서 request.getTemplateId()를 꺼내더라도 이 메서드로 넘기지 않아도 됨)

	// TODO: completed 토글 (PATCH /characters/{characterId})
	//  - public Character toggleCompleted(Long characterId) { ... }
	//  - Optional<Character> selectedCharacter = characterRepository.findById(characterId)
	//  - 없으면 throw new ResourceNotFoundException("CHARACTER_NOT_FOUND", "해당 캐릭터를 찾을 수 없습니다.");
	//  - 조회한 엔티티의 toggleCompleted() 호출 (Character.java에 만들어 둔 메서드)
	//  - GameService.updateGame과 동일하게 save() 호출 없이 dirty checking으로 자동 반영됨

	// TODO: 캐릭터 삭제 (DELETE /characters/{characterId})
	//  - public void deleteCharacter(Long characterId) { ... }
	//  - GameService.deleteGame과 동일한 패턴:
	//    if (!characterRepository.existsById(characterId)) {
	//        throw new ResourceNotFoundException("CHARACTER_NOT_FOUND", "해당 캐릭터를 찾을 수 없습니다.");
	//    }
	//    characterRepository.deleteById(characterId);

}
