package com.erensia.gamescheduling.charactertemplate;

import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import com.erensia.gamescheduling.game.Game;
import com.erensia.gamescheduling.game.GameRepository;
import com.erensia.gamescheduling.templateitem.TemplateItem;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CharacterTemplate 관련 비즈니스 로직 계층.
 * CharacterService/WeeklyContentService와 동일한 패턴: 컨트롤러는 이 서비스만 호출하고,
 * 이 서비스가 CharacterTemplateRepository/GameRepository를 호출해 DB와 상호작용한다.
 *
 * 주의: 람다식 금지 컨벤션(CLAUDE.md) - itemTexts 같은 목록을 순회할 때는 Stream이 아니라
 * for문/if문을 사용할 것.
 */
@Service
@RequiredArgsConstructor
public class CharacterTemplateService {

	private final CharacterTemplateRepository characterTemplateRepository;
	private final GameRepository gameRepository;

	/**
	 * 게임별 템플릿 목록 조회 (GET /games/{gameId}/templates).
	 * TODO: CharacterService.getCharacters(Long gameId, Boolean completed)와 동일한 패턴 -
	 *   gameId로 게임 존재를 먼저 검증하고(없으면 GAME_NOT_FOUND), 존재하면
	 *   characterTemplateRepository.findByGameIdOrderByIdAsc(gameId)를 반환하세요.
	 */
	public List<CharacterTemplate> getTemplates(Long gameId) {
		// TODO: 구현하세요.
		return null;
	}

	/**
	 * 템플릿 생성 (POST /games/{gameId}/templates).
	 * TODO:
	 *   1) gameId로 Game을 조회한다 (없으면 GAME_NOT_FOUND) - CharacterService.createCharacter 참고.
	 *   2) new CharacterTemplate(game, name)으로 템플릿을 만든다.
	 *   3) itemTexts를 for문으로 순회하며 new TemplateItem(template, text)를 만들어
	 *      템플릿의 templateItems 컬렉션에 추가한다.
	 *   4) characterTemplateRepository.save(template)로 저장하고 반환한다.
	 *      (CharacterTemplate.templateItems의 cascade 옵션이 올바르게 설정돼 있어야
	 *      항목까지 함께 insert된다 - CharacterTemplate.java의 TODO 주석 참고)
	 */
	public CharacterTemplate createTemplate(Long gameId, String name, List<String> itemTexts) {
		// TODO: 구현하세요.
		return null;
	}

	/**
	 * 템플릿 삭제 (DELETE /templates/{templateId}).
	 * TODO: CharacterService.deleteCharacter(Long characterId)와 동일한 패턴 -
	 *   존재하지 않으면 TEMPLATE_NOT_FOUND, 존재하면 deleteById.
	 */
	public void deleteTemplate(Long templateId) {
		// TODO: 구현하세요.
	}

}
