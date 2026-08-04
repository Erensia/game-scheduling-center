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
 * 저장하지 않고, toggleCompletion()에서 매번 game.resetDay + 오늘 날짜로 계산한다.
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
		int resetDay = weeklyContent.getGame().getResetDay();
		int presentDay = today.getDayOfWeek().getValue();
		int diff = ((presentDay % 7) - resetDay + 7) % 7;
		LocalDate currentWeekStart = today.minusDays(diff);
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
