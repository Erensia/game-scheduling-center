package com.erensia.gamescheduling.checklistitem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.erensia.gamescheduling.character.Character;
import com.erensia.gamescheduling.character.CharacterRepository;
import com.erensia.gamescheduling.common.exception.ResourceNotFoundException;
import com.erensia.gamescheduling.game.Game;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ChecklistItemService 단위 테스트.
 *
 * CharacterServiceTest와 동일한 패턴: ChecklistItemRepository/CharacterRepository를 @Mock으로
 * 대체하고, @InjectMocks로 ChecklistItemService에 주입한다. ChecklistItemService는
 * createItem에서만 CharacterRepository를 쓰고(캐릭터 존재 확인), toggleDone/deleteItem은
 * ChecklistItemRepository만 쓴다 - ChecklistItemService.java 참고.
 */
@ExtendWith(MockitoExtension.class)
class ChecklistItemServiceTest {

	@Mock
	private ChecklistItemRepository checklistItemRepository;

	@Mock
	private CharacterRepository characterRepository;

	@InjectMocks
	private ChecklistItemService checklistItemService;

	private Game existingGame;

	private Character existingCharacter;

	private ChecklistItem existingItem;

	@BeforeEach
	void setUp() {
		existingGame = new Game("wuwa", 1, 3);
		existingCharacter = new Character(existingGame, "장리");
		existingItem = new ChecklistItem(existingCharacter, "checklist Demo");
	}

	@Test
	void createItem_캐릭터가_존재하면_항목을_생성한다() {
		when(characterRepository.findById(1L)).thenReturn(Optional.of(existingCharacter));
		when(checklistItemRepository.save(any(ChecklistItem.class))).thenReturn(existingItem);
		String inputText = "checklist Demo";

		checklistItemService.createItem(1L, inputText);

		ArgumentCaptor<ChecklistItem> captor = ArgumentCaptor.forClass(ChecklistItem.class);
		verify(checklistItemRepository).save(captor.capture());
		assertThat(captor.getValue().getCharacter()).isEqualTo(existingCharacter);
		assertThat(captor.getValue().getText()).isEqualTo(inputText);
	}

	@Test
	void createItem_캐릭터가_존재하지_않으면_CHARACTER_NOT_FOUND를_던진다() {
		when(characterRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> checklistItemService.createItem(1L, "text"))
		.isInstanceOf(ResourceNotFoundException.class);
		verify(checklistItemRepository, never()).save(any(ChecklistItem.class));
	}

	@Test
	void toggleDone_존재하는_항목이면_done을_반전시킨다() {
		when(checklistItemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

		ChecklistItem result = checklistItemService.toggleDone(1L);

		assertThat(result.isDone()).isEqualTo(true);
	}

	@Test
	void toggleDone_존재하지_않으면_ITEM_NOT_FOUND를_던진다() {
		when(checklistItemRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> checklistItemService.toggleDone(1L))
		.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deleteItem_존재하는_항목이면_삭제한다() {
		when(checklistItemRepository.existsById(1L)).thenReturn(true);

		checklistItemService.deleteItem(1L);

		verify(checklistItemRepository).deleteById(1L);
	}

	@Test
	void deleteItem_존재하지_않으면_ITEM_NOT_FOUND를_던진다() {
		when(checklistItemRepository.existsById(1L)).thenReturn(false);
		assertThatThrownBy(() -> checklistItemService.deleteItem(1L))
		.isInstanceOf(ResourceNotFoundException.class);
		verify(checklistItemRepository, never()).deleteById(1L);
	}

}
