package com.erensia.gamescheduling.checklistitem;

import com.erensia.gamescheduling.character.Character;
import com.erensia.gamescheduling.character.CharacterRepository;
import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ChecklistItem 관련 비즈니스 로직 계층.
 * CharacterService와 동일한 패턴: 컨트롤러는 이 서비스만 호출하고, 이 서비스가
 * ChecklistItemRepository/CharacterRepository를 호출해 DB와 상호작용한다.
 */
@Service
@RequiredArgsConstructor
public class ChecklistItemService {

	private final ChecklistItemRepository checklistItemRepository;
	private final CharacterRepository characterRepository;

	/**
	 * 항목 추가 (POST /characters/{characterId}/items).
	 * CharacterService.createCharacter(Long gameId, String name)와 동일한 패턴:
	 * characterId로 Character를 조회 못하면 CHARACTER_NOT_FOUND 예외.
	 */
	public ChecklistItem createItem(Long characterId, String text) {
		Optional<Character> selectedCharacter = characterRepository.findById(characterId);
		if (selectedCharacter.isEmpty()) {
			throw new ResourceNotFoundException("CHARACTER_NOT_FOUND", "해당 캐릭터를 찾을 수 없습니다.");
		}
		Character character = selectedCharacter.get();
		ChecklistItem item = new ChecklistItem(character, text);

		return checklistItemRepository.save(item);
	}

	/**
	 * done 토글 (PATCH /items/{itemId}).
	 * CharacterService.toggleCompleted(Long characterId)와 동일한 패턴 (dirty checking, @Transactional).
	 */
	@Transactional
	public ChecklistItem toggleDone(Long itemId) {
		Optional<ChecklistItem> selectedChecklistItem = checklistItemRepository.findById(itemId);
		if (selectedChecklistItem.isEmpty()) {
			throw new ResourceNotFoundException("ITEM_NOT_FOUND", "해당 체크리스트 항목을 찾을 수 없습니다.");
		}
		ChecklistItem item = selectedChecklistItem.get();
		item.toggleDone();

		return item;
	}

	/**
	 * 항목 삭제 (DELETE /items/{itemId}).
	 * CharacterService.deleteCharacter(Long characterId)와 동일한 패턴.
	 */
	public void deleteItem(Long itemId) {
		if (!checklistItemRepository.existsById(itemId)) {
			throw new ResourceNotFoundException("ITEM_NOT_FOUND", "해당 체크리스트 항목을 찾을 수 없습니다.");
		}
		checklistItemRepository.deleteById(itemId);
	}

}
