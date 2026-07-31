package com.erensia.gamescheduling.character;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import com.erensia.gamescheduling.game.Game;
import com.erensia.gamescheduling.game.GameRepository;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CharacterService 단위 테스트 (스켈레톤).
 *
 * GameServiceTest와 동일한 패턴: CharacterRepository/GameRepository를 @Mock으로 대체하고,
 * @InjectMocks로 CharacterService에 주입한다. CharacterService는 GameRepository도 함께
 * 의존하므로(게임 존재 여부 확인용) 두 repository 모두 mock이 필요하다.
 *
 * TODO: 아래 각 테스트 메서드에 given(when 스텁) - when(실행) - then(검증) 채우기.
 * 예외 케이스는 GameServiceTest처럼 assertThatThrownBy(() -> ...).isInstanceOf(...) 사용.
 */
@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

	@Mock
	private CharacterRepository characterRepository;

	@Mock
	private GameRepository gameRepository;

	@InjectMocks
	private CharacterService characterService;

	private Game existingGame;

	private Character existingCharacter;

	@BeforeEach
	void setUp() {
		// TODO: existingGame, existingCharacter 초기화 (GameServiceTest의 existingGame 참고)
		existingGame = new Game("zzz", 1, 3);
		existingCharacter = new Character(existingGame, "제인 도");
	}

	@Test
	void getCharacters_completed가_null이면_게임의_전체_캐릭터_목록을_반환한다() {
		// TODO: gameRepository.existsById(gameId) -> true 스텁
		// TODO: characterRepository.findByGameIdOrderByIdAsc(gameId) -> 목록 스텁
		// TODO: characterService.getCharacters(gameId, null) 호출
		// TODO: 결과 검증 + findByGameIdAndCompletedOrderByIdAsc는 호출되지 않았는지 검증(verify never)
		when(gameRepository.existsById(1L)).thenReturn(true);
		List<Character> result = characterRepository.findByGameIdOrderByIdAsc(1L);
		characterService.getCharacters(1L, null);
		verify(characterRepository, never()).findByGameIdAndCompletedOrderByIdAsc(1L, false);
	}

	@Test
	void getCharacters_completed가_있으면_필터링된_목록을_반환한다() {
		// TODO: gameRepository.existsById(gameId) -> true 스텁
		// TODO: characterRepository.findByGameIdAndCompletedOrderByIdAsc(gameId, completed) -> 목록 스텁
		// TODO: characterService.getCharacters(gameId, true) 호출 후 검증
	}

	@Test
	void getCharacters_게임이_존재하지_않으면_GAME_NOT_FOUND를_던진다() {
		// TODO: gameRepository.existsById(gameId) -> false 스텁
		// TODO: assertThatThrownBy(...).isInstanceOf(ResourceNotFoundException.class) 로 검증
		// TODO: characterRepository는 전혀 호출되지 않았는지 verify(never())로 확인
	}

	@Test
	void createCharacter_게임이_존재하면_캐릭터를_생성한다() {
		// TODO: gameRepository.findById(gameId) -> Optional.of(existingGame) 스텁
		// TODO: characterRepository.save(any(Character.class)) -> existingCharacter 스텁
		// TODO: characterService.createCharacter(gameId, name) 호출 후 결과 검증
		// TODO: ArgumentCaptor로 save()에 넘어간 Character의 game/name이 기대값인지 검증 (GameServiceTest의 createGame 테스트 참고)
	}

	@Test
	void createCharacter_게임이_존재하지_않으면_GAME_NOT_FOUND를_던진다() {
		// TODO: gameRepository.findById(gameId) -> Optional.empty() 스텁
		// TODO: assertThatThrownBy(...) 로 예외 검증
		// TODO: characterRepository.save는 호출되지 않았는지 verify(never())로 확인
	}

	@Test
	void toggleCompleted_존재하는_캐릭터면_completed를_반전시킨다() {
		// TODO: characterRepository.findById(characterId) -> Optional.of(existingCharacter) 스텁
		// TODO: characterService.toggleCompleted(characterId) 호출
		// TODO: 반환된 character.isCompleted()가 반전됐는지 검증
	}

	@Test
	void toggleCompleted_존재하지_않으면_CHARACTER_NOT_FOUND를_던진다() {
		// TODO: characterRepository.findById(characterId) -> Optional.empty() 스텁
		// TODO: assertThatThrownBy(...) 로 예외 검증 (code가 "CHARACTER_NOT_FOUND"인지까지 확인하면 더 좋음)
	}

	@Test
	void deleteCharacter_존재하는_캐릭터면_삭제한다() {
		// TODO: characterRepository.existsById(characterId) -> true 스텁
		// TODO: characterService.deleteCharacter(characterId) 호출
		// TODO: verify(characterRepository).deleteById(characterId)
	}

	@Test
	void deleteCharacter_존재하지_않으면_CHARACTER_NOT_FOUND를_던진다() {
		// TODO: characterRepository.existsById(characterId) -> false 스텁
		// TODO: assertThatThrownBy(...) 로 예외 검증
		// TODO: verify(characterRepository, never()).deleteById(characterId)
	}

}
