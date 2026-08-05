package com.erensia.gamescheduling.weeklycontent;

import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import com.erensia.gamescheduling.game.Game;
import com.erensia.gamescheduling.game.GameRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * WeeklyContent 관련 비즈니스 로직 계층.
 * CharacterService와 동일한 패턴: 컨트롤러는 이 서비스만 호출하고, 이 서비스가
 * WeeklyContentRepository/GameRepository를 호출해 DB와 상호작용한다.
 *
 * 이 도메인만의 책임: "이번 주 시작일" 계산. 03-erd.md 설계 메모 결정에 따라 DB에는
 * 저장하지 않고, calculateWeekStart()에서 매번 game.resetDay + 오늘 날짜로 계산한다
 * (isCompletedThisWeek(), toggleCompletion()이 공통으로 이 계산을 사용한다).
 */
@Service
@RequiredArgsConstructor
public class WeeklyContentService {

	private final WeeklyContentRepository weeklyContentRepository;
	private final GameRepository gameRepository;

	/**
	 * 게임별 주간 컨텐츠 목록 조회 (GET /games/{gameId}/weekly).
	 * CharacterService.getCharacters()와 동일한 패턴 - gameId 존재 검증 후 조회.
	 */
	public List<WeeklyContent> getWeeklyContents(Long gameId) {
		if (!gameRepository.existsById(gameId)) {
			throw new ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다.");
		}
		return weeklyContentRepository.findByGameIdOrderByIdAsc(gameId);
	}

	/**
	 * 주간 컨텐츠 생성 (POST /games/{gameId}/weekly).
	 * CharacterService.createCharacter(Long gameId, String name)와 동일한 패턴.
	 */
	public WeeklyContent createWeeklyContent(Long gameId, String name) {
		Optional<Game> selectedGame = gameRepository.findById(gameId);
		if (selectedGame.isEmpty()) {
			throw new ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다.");
		}
		Game game = selectedGame.get();
		WeeklyContent weeklyContent = new WeeklyContent(game, name);

		return weeklyContentRepository.save(weeklyContent);
	}

	/**
	 * "이번 주에 완료했는지" 여부 계산 (GET /games/{gameId}/weekly 응답의 completedThisWeek 값).
	 * 컨트롤러가 매번 오늘 날짜를 직접 구해서 넘기지 않아도 되도록 만든 편의 메서드 - 오늘 날짜를
	 * 이 메서드가 알아서 구해서, 바로 아래의 (WeeklyContent, LocalDate) 오버로딩 메서드에 위임한다.
	 */
	public boolean isCompletedThisWeek(WeeklyContent weeklyContent) {
		LocalDate today = LocalDate.now();
		return isCompletedThisWeek(weeklyContent, today);
	}

	/**
	 * 게임의 resetDay와 기준 날짜(today)로 "이번 주 시작일"을 계산한다.
	 * isCompletedThisWeek(WeeklyContent, LocalDate)와 toggleCompletion()이 공통으로 사용하는
	 * private 헬퍼 - 두 메서드에 같은 계산을 중복시키지 않기 위해 분리했다.
	 *
	 * java.time.DayOfWeek.getValue()는 월=1~일=7(ISO) 기준이지만, 이 프로젝트의 resetDay는
	 * 일=0~토=6 기준이라 두 값이 어긋난다. presentDay % 7로 일요일(ISO 7)만 0으로 맞춰 이 차이를
	 * 흡수한 뒤, resetDay와의 차이(diff)만큼 today에서 빼서 가장 최근 리셋 요일의 날짜를 구한다.
	 */
	private LocalDate calculateWeekStart(WeeklyContent weeklyContent, LocalDate today) {
		int resetDay = weeklyContent.getGame().getResetDay();
		int presentDay = today.getDayOfWeek().getValue();
		int diff = ((presentDay % 7) - resetDay + 7) % 7;

		return today.minusDays(diff);
	}

	/**
	 * isCompletedThisWeek(WeeklyContent)와 동일한 계산이지만, "오늘"을 파라미터로 받는 버전.
	 * LocalDate.now()에 의존하지 않으므로, 테스트에서 원하는 날짜를 직접 넣어 검증할 수 있다.
	 */
	public boolean isCompletedThisWeek(WeeklyContent weeklyContent, LocalDate today) {
		LocalDate currentWeekStart = calculateWeekStart(weeklyContent, today);
		LocalDate lastCompletedWeek = weeklyContent.getLastCompletedWeekStart();

		return currentWeekStart.equals(lastCompletedWeek);
	}

	/**
	 * 완료 상태 토글 (PATCH /weekly/{weeklyId}/toggle).
	 * CharacterService.toggleCompleted()와의 차이점: 토글하기 전에 "이번 주 시작일"부터 계산해야 한다.
	 */
	@Transactional
	public WeeklyContent toggleCompletion(Long weeklyId) {
		Optional<WeeklyContent> selectedWeeklyContent = weeklyContentRepository.findById(weeklyId);
		if (selectedWeeklyContent.isEmpty()) {
			throw new ResourceNotFoundException("WEEKLY_CONTENT_NOT_FOUND", "주간 컨텐츠를 찾을 수 없습니다.");
		}
		WeeklyContent weeklyContent = selectedWeeklyContent.get();
		LocalDate today = LocalDate.now();
		LocalDate currentWeekStart = calculateWeekStart(weeklyContent, today);
		weeklyContent.toggleWeeklyContent(currentWeekStart);

		return weeklyContent;
	}

	/**
	 * 주간 컨텐츠 삭제 (DELETE /weekly/{weeklyId}).
	 * CharacterService.deleteCharacter()와 동일한 패턴.
	 */
	public void deleteWeeklyContent(Long weeklyId) {
		if (!weeklyContentRepository.existsById(weeklyId)) {
			throw new ResourceNotFoundException("WEEKLY_CONTENT_NOT_FOUND", "주간 컨텐츠를 찾을 수 없습니다.");
		}
		weeklyContentRepository.deleteById(weeklyId);
	}

}
