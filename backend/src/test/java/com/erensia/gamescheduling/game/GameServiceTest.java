package com.erensia.gamescheduling.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
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
 * GameService 단위 테스트.
 *
 * 목적: GameRepository(DB)를 실제로 띄우지 않고, GameService의 "비즈니스 로직"만 검증한다.
 * 06-tech-stack.md 비기능 요구사항 - "서비스 레이어 단위 테스트 최소 확보"에 대응하는 테스트.
 *
 * 방법: @Mock으로 가짜 GameRepository를 만들고, @InjectMocks로 그 가짜 repository를
 * GameService 생성자에 주입한다. when(...).thenReturn(...) 으로 "이 메서드가 호출되면
 * 이 값을 반환해라"를 미리 정해두고(스텁), GameService 메서드를 호출한 뒤 결과를 검증한다.
 *
 * 예외 케이스는 assertThatThrownBy(...).isInstanceOf(...) 로 검증한다.
 * (참고: assertThrows(Class, Executable)를 쓰면 람다가 필요해지므로, 이 프로젝트는
 *  람다 없이 쓸 수 있는 AssertJ의 assertThatThrownBy 체이닝 방식을 쓴다)
 */
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

	@Mock
	private GameRepository gameRepository;

	@InjectMocks
	private GameService gameService;

	private Game existingGame;

	@BeforeEach
	void setUp() {
		existingGame = new Game("WW", 1, 3);
	}

	@Test
	void createGame_요청받은_값으로_게임을_생성한다() {
		when(gameRepository.save(any(Game.class))).thenReturn(existingGame);

		Game result = gameService.createGame("WW", 1, 3);

		assertThat(result.getName()).isEqualTo(existingGame.getName());
		assertThat(result.getResetDay()).isEqualTo(existingGame.getResetDay());
		assertThat(result.getPartySize()).isEqualTo(existingGame.getPartySize());

		ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
		verify(gameRepository).save(captor.capture());
		assertThat(captor.getValue().getName()).isEqualTo("WW");
		assertThat(captor.getValue().getResetDay()).isEqualTo(1);
		assertThat(captor.getValue().getPartySize()).isEqualTo(3);
	}

	@Test
	void getAllGames_저장된_게임_목록을_그대로_반환한다() {
		List<Game> games = List.of(existingGame);

		when(gameRepository.findAll()).thenReturn(games);

		List<Game> result = gameService.getAllGames();

		assertThat(result).hasSize(1);
		assertThat(result).isEqualTo(games);
	}

	@Test
	void updateGame_존재하는_게임이면_설정을_수정한다() {
		when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));

		Game result = gameService.updateGame(1L, 2, 4);

		assertThat(result.getName()).isEqualTo("WW");
		assertThat(result.getResetDay()).isEqualTo(2);
		assertThat(result.getPartySize()).isEqualTo(4);

		verify(gameRepository).findById(1L);
	}

	@Test
	void updateGame_존재하지_않는_게임이면_ResourceNotFoundException을_던진다() {
		when(gameRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> gameService.updateGame(1L, 2, 4))
		.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deleteGame_존재하는_게임이면_삭제한다() {
		when(gameRepository.existsById(1L)).thenReturn(true);
		gameService.deleteGame(1L);
		verify(gameRepository).deleteById(1L);
	}

	@Test
	void deleteGame_존재하지_않는_게임이면_ResourceNotFoundException을_던진다() {
		when(gameRepository.existsById(1L)).thenReturn(false);
		assertThatThrownBy(() -> gameService.deleteGame(1L))
		.isInstanceOf(ResourceNotFoundException.class);
		verify(gameRepository, never()).deleteById(1L);
	}

}
