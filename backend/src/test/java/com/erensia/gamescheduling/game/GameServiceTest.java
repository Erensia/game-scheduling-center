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
 * 진행 방법 (TODO 채우는 순서 추천):
 *  1. createGame_성공 케이스부터 채워서 Mockito when/verify 패턴에 먼저 익숙해지기
 *  2. updateGame/deleteGame의 "존재하는 경우"와 "존재하지 않는 경우" 두 갈래를 각각 테스트
 *  3. 예외 케이스는 assertThatThrownBy(...).isInstanceOf(...) 로 검증
 *     (참고: assertThrows(Class, Executable)를 쓰면 람다가 필요해지므로, 이 프로젝트는
 *      람다 없이 쓸 수 있는 AssertJ의 assertThatThrownBy 체이닝 방식을 쓴다)
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
		// TODO: 테스트마다 재사용할 Game 객체 하나 준비 (예: new Game("명조", 3, 3))
		// 주의: Game.id는 BaseEntity가 자동 채번하므로, 실제 DB 없이 만든 객체는 id가 null이다.
		// updateGame/deleteGame 테스트에서 "특정 id로 조회됐다"를 흉내내려면
		// gameRepository.findById(id)의 리턴값을 Mockito로 직접 스텁해야 한다 (id 자체는 신경쓰지 않아도 됨).
		existingGame = new Game("WW",1,3);
	}

	@Test
	void createGame_요청받은_값으로_게임을_생성한다() {
		// TODO 1. gameRepository.save(...)가 호출되면 특정 Game을 리턴하도록 스텁 (when(...).thenReturn(...))
		// TODO 2. gameService.createGame(name, resetDay, partySize) 호출
		// TODO 3. 리턴된 Game의 필드값이 요청한 값과 일치하는지 assertThat(...)으로 검증
		// TODO 4. verify(gameRepository).save(...) 로 save가 실제로 호출됐는지 확인 (선택)
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
		// TODO 1. gameRepository.findAll()이 리턴할 List<Game> 준비 및 스텁
		// TODO 2. gameService.getAllGames() 호출
		// TODO 3. 반환된 리스트 크기/내용이 스텁한 값과 일치하는지 검증
		List<Game> games = List.of(existingGame);
		
		when(gameRepository.findAll()).thenReturn(games);
		
		List<Game> result = gameService.getAllGames();
		
		assertThat(result).hasSize(1);
		assertThat(result).isEqualTo(games);
	}

	@Test
	void updateGame_존재하는_게임이면_설정을_수정한다() {
		// TODO 1. gameRepository.findById(임의의_id)가 Optional.of(existingGame)을 리턴하도록 스텁
		// TODO 2. gameService.updateGame(id, newResetDay, newPartySize) 호출
		// TODO 3. existingGame의 resetDay/partySize가 새 값으로 바뀌었는지 검증
		//         (GameService가 save()를 다시 호출하지 않는다는 점도 03-erd.md/GameService 주석의
		//          "dirty checking" 설명과 같이 이해해두면 좋다 - 검증까지 할 필요는 없음)
		when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));
		
		Game result = gameService.updateGame(1L, 2, 4);
		
		assertThat(result.getName()).isEqualTo("WW");
		assertThat(result.getResetDay()).isEqualTo(2);
		assertThat(result.getPartySize()).isEqualTo(4);
		
		verify(gameRepository).findById(1L);		
	}

	@Test
	void updateGame_존재하지_않는_게임이면_ResourceNotFoundException을_던진다() {
		// TODO 1. gameRepository.findById(임의의_id)가 Optional.empty()를 리턴하도록 스텁
		// TODO 2. assertThatThrownBy(() -> gameService.updateGame(...)) 로 예외 발생 검증
		//         (여기서는 예외 타입 자체를 검증하는 것이 테스트의 핵심이라 람다 사용이 자연스러움 -
		//          AssertJ/JUnit 예외 검증 API의 관례이지, "로직"에 람다를 쓰는 것과는 다른 경우)
		// TODO 3. .isInstanceOf(ResourceNotFoundException.class) 로 예외 타입 검증
		when(gameRepository.findById(1L)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> gameService.updateGame(1L,2,4))
		.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deleteGame_존재하는_게임이면_삭제한다() {
		// TODO 1. gameRepository.existsById(id)가 true를 리턴하도록 스텁
		// TODO 2. gameService.deleteGame(id) 호출
		// TODO 3. verify(gameRepository).deleteById(id) 로 삭제가 호출됐는지 검증
		when(gameRepository.existsById(1L)).thenReturn(true);
		gameService.deleteGame(1L);
		verify(gameRepository).deleteById(1L);
	}

	@Test
	void deleteGame_존재하지_않는_게임이면_ResourceNotFoundException을_던진다() {
		// TODO 1. gameRepository.existsById(id)가 false를 리턴하도록 스텁
		// TODO 2. assertThatThrownBy로 예외 발생 및 타입 검증
		// TODO 3. verify(gameRepository, never()).deleteById(anyLong()) 로 삭제가 호출되지 "않았는지" 검증 (선택)
		when(gameRepository.existsById(1L)).thenReturn(false);
		assertThatThrownBy(() -> gameService.deleteGame(1L))
		.isInstanceOf(ResourceNotFoundException.class);
		verify(gameRepository, never()).deleteById(1L);
		
	}

}
