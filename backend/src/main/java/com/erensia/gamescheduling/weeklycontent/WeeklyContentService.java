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
		// TODO: gameRepository.existsById(gameId)로 게임 존재 검증.
		//       존재하지 않으면 ResourceNotFoundException("GAME_NOT_FOUND", "해당 게임을 찾을 수 없습니다.") 던지기.
		// TODO: weeklyContentRepository의 게임별 목록 조회 메서드(WeeklyContentRepository에서 작성한 것) 호출 후 반환.
		return null;
	}

	/**
	 * 주간 컨텐츠 생성 (POST /games/{gameId}/weekly).
	 * CharacterService.createCharacter(Long gameId, String name)와 동일한 패턴.
	 */
	public WeeklyContent createWeeklyContent(Long gameId, String name) {
		// TODO: gameRepository.findById(gameId)로 Game 조회.
		//       없으면 ResourceNotFoundException("GAME_NOT_FOUND", ...) 던지기.
		// TODO: new WeeklyContent(game, name)으로 엔티티 생성 후 weeklyContentRepository.save()로 저장, 반환.
		return null;
	}

	/**
	 * 완료 상태 토글 (PATCH /weekly/{weeklyId}/toggle).
	 * CharacterService.toggleCompleted()와의 차이점: 토글하기 전에 "이번 주 시작일"부터 계산해야 한다.
	 */
	@Transactional
	public WeeklyContent toggleCompletion(Long weeklyId) {
		// TODO: weeklyContentRepository.findById(weeklyId)로 조회.
		//       없으면 ResourceNotFoundException("WEEKLY_CONTENT_NOT_FOUND", ...) 던지기.
		//
		// TODO: weeklyContent.getGame().getResetDay()를 이용해 "이번 주 시작일"(LocalDate)을 계산하세요.
		//       프론트엔드 app.js의 weekKeyFor(resetDay) 로직(19번째 줄)을 자바로 옮기는 작업입니다:
		//         var diff = (오늘요일 - resetDay + 7) % 7;
		//         결과 = 오늘 날짜에서 diff일을 뺀 날짜
		//       힌트: LocalDate.now(), LocalDate.getDayOfWeek()(월=1~일=7, DayOfWeek.getValue()),
		//       그런데 이 프로젝트의 resetDay는 0=일요일~6=토요일이라 요일 값 변환에 주의하세요.
		//       LocalDate.minusDays(long)로 날짜를 뺍니다.
		//
		// TODO: weeklyContent.toggleCompletion(계산한주시작일) 호출.
		//       @Transactional + dirty checking이라 별도 save() 호출은 필요 없습니다.
		return null;
	}

	/**
	 * 주간 컨텐츠 삭제 (DELETE /weekly/{weeklyId}).
	 * CharacterService.deleteCharacter()와 동일한 패턴.
	 */
	public void deleteWeeklyContent(Long weeklyId) {
		// TODO: weeklyContentRepository.existsById(weeklyId)로 존재 검증.
		//       없으면 ResourceNotFoundException("WEEKLY_CONTENT_NOT_FOUND", ...) 던지기.
		// TODO: weeklyContentRepository.deleteById(weeklyId) 호출.
	}

}
