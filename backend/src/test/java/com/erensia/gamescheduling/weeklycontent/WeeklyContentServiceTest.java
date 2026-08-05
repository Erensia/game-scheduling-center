package com.erensia.gamescheduling.weeklycontent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import com.erensia.gamescheduling.game.Game;
import com.erensia.gamescheduling.game.GameRepository;
import java.time.LocalDate;
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
 * WeeklyContentService 단위 테스트.
 *
 * CharacterServiceTest와 동일한 패턴: WeeklyContentRepository/GameRepository를 @Mock으로
 * 대체하고, @InjectMocks로 WeeklyContentService에 주입한다.
 *
 * calculateWeekStart()가 private이라 직접 테스트할 수 없으므로, isCompletedThisWeek(WeeklyContent,
 * LocalDate) 오버로드를 통해 간접적으로 검증한다. resetDay=1(월요일) 기준으로 오늘이 리셋 요일 당일/
 * 다음날/전날인 세 가지 경우를 각각 별도 테스트로 분리했다 (toggleWeeklyContent는 setter가 아니라
 * 토글이라, 한 테스트 메서드 안에서 여러 케이스를 이어서 검증하면 상태가 꼬이기 때문).
 */
@ExtendWith(MockitoExtension.class)
class WeeklyContentServiceTest {

	@Mock
	private WeeklyContentRepository weeklyContentRepository;

	@Mock
	private GameRepository gameRepository;

	@InjectMocks
	private WeeklyContentService weeklyContentService;

	private Game existingGame;

	private WeeklyContent existingWeeklyContent;

	@BeforeEach
	void setUp() {
		existingGame = new Game("wuwa", 1, 3);
		existingWeeklyContent = new WeeklyContent(existingGame, "content");
	}

	@Test
	void getWeeklyContents_게임이_존재하면_게임의_전체_주간컨텐츠_목록을_반환한다() {
		List<WeeklyContent> compares = List.of(existingWeeklyContent);
		when(gameRepository.existsById(1L)).thenReturn(true);
		when(weeklyContentRepository.findByGameIdOrderByIdAsc(1L)).thenReturn(List.of(existingWeeklyContent));

		List<WeeklyContent> results = weeklyContentService.getWeeklyContents(1L);

		assertThat(results).hasSize(1);
		assertThat(results).isEqualTo(compares);
	}

	@Test
	void getWeeklyContents_게임이_존재하지_않으면_GAME_NOT_FOUND를_던진다() {
		when(gameRepository.existsById(1L)).thenReturn(false);

		assertThatThrownBy(() -> weeklyContentService.getWeeklyContents(1L))
				.isInstanceOf(ResourceNotFoundException.class);
		verify(weeklyContentRepository, never()).findByGameIdOrderByIdAsc(1L);
	}

	@Test
	void createWeeklyContent_게임이_존재하면_주간컨텐츠를_생성한다() {
		when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));
		when(weeklyContentRepository.save(any(WeeklyContent.class))).thenReturn(existingWeeklyContent);

		weeklyContentService.createWeeklyContent(1L, "content");

		ArgumentCaptor<WeeklyContent> captor = ArgumentCaptor.forClass(WeeklyContent.class);
		verify(weeklyContentRepository).save(captor.capture());
		assertThat(captor.getValue().getGame()).isEqualTo(existingGame);
		assertThat(captor.getValue().getName()).isEqualTo("content");
	}

	@Test
	void createWeeklyContent_게임이_존재하지_않으면_GAME_NOT_FOUND를_던진다() {
		when(gameRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> weeklyContentService.createWeeklyContent(1L, "content"))
				.isInstanceOf(ResourceNotFoundException.class);
		verify(weeklyContentRepository, never()).save(any(WeeklyContent.class));
	}

	@Test
	void isCompletedThisWeek_lastCompletedWeekStart가_null이면_false를_반환한다() {
		LocalDate today = LocalDate.of(2026, 8, 4);

		assertThat(weeklyContentService.isCompletedThisWeek(existingWeeklyContent, today)).isEqualTo(false);
	}

	@Test
	void isCompletedThisWeek_resetDay_오늘이_리셋_요일_당일인_경우() {
		LocalDate today = LocalDate.of(2026, 8, 3);
		existingWeeklyContent.toggleWeeklyContent(today);

		assertThat(weeklyContentService.isCompletedThisWeek(existingWeeklyContent, LocalDate.of(2026, 8, 3)))
				.isEqualTo(true);
	}

	@Test
	void isCompletedThisWeek_resetDay_오늘이_리셋_요일_다음날인_경우() {
		LocalDate today = LocalDate.of(2026, 8, 4);
		existingWeeklyContent.toggleWeeklyContent(LocalDate.of(2026, 8, 3));

		assertThat(weeklyContentService.isCompletedThisWeek(existingWeeklyContent, today)).isEqualTo(true);
	}

	@Test
	void isCompletedThisWeek_resetDay_오늘이_리셋_요일_전날인_경우() {
		LocalDate today = LocalDate.of(2026, 8, 2);
		existingWeeklyContent.toggleWeeklyContent(LocalDate.of(2026, 7, 27));

		assertThat(weeklyContentService.isCompletedThisWeek(existingWeeklyContent, today)).isEqualTo(true);
	}

	@Test
	void toggleCompletion_존재하는_컨텐츠면_완료_상태를_토글한다() {
		when(weeklyContentRepository.findById(1L)).thenReturn(Optional.of(existingWeeklyContent));
		LocalDate beforeToggle = existingWeeklyContent.getLastCompletedWeekStart();

		WeeklyContent afterToggle = weeklyContentService.toggleCompletion(1L);

		assertThat(afterToggle.getLastCompletedWeekStart()).isNotEqualTo(beforeToggle);
	}

	@Test
	void toggleCompletion_존재하지_않으면_WEEKLY_CONTENT_NOT_FOUND를_던진다() {
		when(weeklyContentRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> weeklyContentService.toggleCompletion(1L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deleteWeeklyContent_존재하는_컨텐츠면_삭제한다() {
		when(weeklyContentRepository.existsById(1L)).thenReturn(true);

		weeklyContentService.deleteWeeklyContent(1L);

		verify(weeklyContentRepository).deleteById(1L);
	}

	@Test
	void deleteWeeklyContent_존재하지_않으면_WEEKLY_CONTENT_NOT_FOUND를_던진다() {
		when(weeklyContentRepository.existsById(1L)).thenReturn(false);

		assertThatThrownBy(() -> weeklyContentService.deleteWeeklyContent(1L))
				.isInstanceOf(ResourceNotFoundException.class);
		verify(weeklyContentRepository, never()).deleteById(1L);
	}

}
