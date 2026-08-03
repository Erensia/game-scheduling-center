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
 * CharacterService 단위 테스트.
 *
 * GameServiceTest와 동일한 패턴: CharacterRepository/GameRepository를 @Mock으로 대체하고,
 * @InjectMocks로 CharacterService에 주입한다. CharacterService는 GameRepository도 함께
 * 의존하므로(게임 존재 여부 확인용) 두 repository 모두 mock이 필요하다.
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
		existingGame = new Game("zzz", 1, 3);
		existingCharacter = new Character(existingGame, "제인 도");
	}

	@Test
	void getCharacters_completed가_null이면_게임의_전체_캐릭터_목록을_반환한다() {
		List<Character> compare = List.of(existingCharacter);
		when(gameRepository.existsById(1L)).thenReturn(true);
		when(characterRepository.findByGameIdOrderByIdAsc(1L)).thenReturn(List.of(existingCharacter));
		List<Character> result = characterService.getCharacters(1L, null);
		assertThat(result).hasSize(1);
		assertThat(result).isEqualTo(compare);
		verify(characterRepository, never()).findByGameIdAndCompletedOrderByIdAsc(1L, false);
	}

	@Test
	void getCharacters_completed가_있으면_필터링된_목록을_반환한다() {
		List<Character> compare = List.of(existingCharacter);
		when(gameRepository.existsById(1L)).thenReturn(true);
		when(characterRepository.findByGameIdAndCompletedOrderByIdAsc(1L, true)).thenReturn(compare);
		List<Character> result = characterService.getCharacters(1L, true);
		assertThat(result).isEqualTo(compare);
		verify(characterRepository,never()).findByGameIdOrderByIdAsc(1L);
	}

	@Test
	void getCharacters_게임이_존재하지_않으면_GAME_NOT_FOUND를_던진다() {
		when(gameRepository.existsById(1L)).thenReturn(false);
		assertThatThrownBy(() -> characterService.getCharacters(1L, null))
		.isInstanceOf(ResourceNotFoundException.class);
		
		verify(characterRepository,never()).findByGameIdOrderByIdAsc(1L);
		verify(characterRepository,never()).findByGameIdAndCompletedOrderByIdAsc(1L, false);
	}

	@Test
	void createCharacter_게임이_존재하면_캐릭터를_생성한다() {
		when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));
		when(characterRepository.save(any(Character.class))).thenReturn(existingCharacter);
		characterService.createCharacter(1L, "제인 도");
		ArgumentCaptor<Character> captor = ArgumentCaptor.forClass(Character.class);
		verify(characterRepository).save(captor.capture());
		assertThat(captor.getValue().getGame()).isEqualTo(existingGame);
		assertThat(captor.getValue().getName()).isEqualTo("제인 도");
	}

	@Test
	void createCharacter_게임이_존재하지_않으면_GAME_NOT_FOUND를_던진다() {
		when(gameRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> characterService.createCharacter(1L, "제인 도"))
		.isInstanceOf(ResourceNotFoundException.class);
		verify(characterRepository, never()).save(any(Character.class));
	}

	@Test
	void toggleCompleted_존재하는_캐릭터면_completed를_반전시킨다() {
		when(characterRepository.findById(1L)).thenReturn(Optional.of(existingCharacter));
		Character result = characterService.toggleCompleted(1L);
		assertThat(result.isCompleted()).isEqualTo(true);
	}

	@Test
	void toggleCompleted_존재하지_않으면_CHARACTER_NOT_FOUND를_던진다() {
		when(characterRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> characterService.toggleCompleted(1L))
		.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deleteCharacter_존재하는_캐릭터면_삭제한다() {
		when(characterRepository.existsById(1L)).thenReturn(true);
		characterService.deleteCharacter(1L);
		verify(characterRepository).deleteById(1L);
	}

	@Test
	void deleteCharacter_존재하지_않으면_CHARACTER_NOT_FOUND를_던진다() {
		when(characterRepository.existsById(1L)).thenReturn(false);
		assertThatThrownBy(() -> characterService.deleteCharacter(1L))
		.isInstanceOf(ResourceNotFoundException.class);
		verify(characterRepository,never()).deleteById(1L);
	}

}
