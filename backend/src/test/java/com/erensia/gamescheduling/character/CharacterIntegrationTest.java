package com.erensia.gamescheduling.character;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erensia.gamescheduling.game.Game;
import com.erensia.gamescheduling.game.GameRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Character 도메인 통합 테스트 (스켈레톤).
 *
 * GameIntegrationTest와 동일한 패턴: Controller -> Service -> Repository -> 실제 PostgreSQL
 * (Testcontainers)까지 전부 진짜로 붙여서 검증한다.
 *
 * 주의: Character는 Game에 종속되므로(game_id nullable=false), 테스트마다 먼저
 * gameRepository.save(new Game(...))로 부모 Game을 만들어 둔 뒤 그 game의 id를 사용해야 한다.
 *
 * TODO: 아래 각 테스트 메서드에 given(데이터 준비) - when(mockMvc.perform) - then(expect/assertThat) 채우기.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CharacterIntegrationTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CharacterRepository characterRepository;

	@Autowired
	private GameRepository gameRepository;

	@BeforeEach
	void cleanUp() {
		// TODO: characterRepository.deleteAll() 먼저, 그 다음 gameRepository.deleteAll()
		// (FK 순서 주의 - Character가 Game을 참조하므로 자식부터 지워야 함)
	}

	@Test
	void 캐릭터를_생성하면_목록_조회에서_확인할_수_있다() throws Exception {
		// TODO: gameRepository.save(new Game(...))로 부모 게임 준비
		// TODO: CharacterCreateRequest 준비 후 post("/games/{gameId}/characters", gameId) 호출
		// TODO: status().isCreated() 검증
		// TODO: get("/games/{gameId}/characters", gameId) 호출해서 방금 만든 캐릭터가 보이는지 검증
	}

	@Test
	void completed_필터로_캐릭터_목록을_조회할_수_있다() throws Exception {
		// TODO: 게임 하나 준비, 캐릭터 여러 개(completed true/false 섞어서) DB에 직접 저장
		//       (characterRepository.save(new Character(game, name)) 후 필요하면 toggleCompleted() 호출)
		// TODO: get("/games/{gameId}/characters?completed=true", gameId) 호출
		// TODO: 결과에 completed=true인 캐릭터만 포함되는지 검증
	}

	@Test
	void 존재하지_않는_게임에_캐릭터를_생성하면_404_GAME_NOT_FOUND를_반환한다() throws Exception {
		// TODO: CharacterCreateRequest 준비
		// TODO: post("/games/{gameId}/characters", 존재하지_않는_id) 호출
		// TODO: status().isNotFound(), jsonPath("$.code").value("GAME_NOT_FOUND") 검증
	}

	@Test
	void 캐릭터의_completed를_토글할_수_있다() throws Exception {
		// TODO: 게임 + 캐릭터 준비 (초기 completed = false)
		// TODO: patch("/characters/{characterId}", characterId) 호출
		// TODO: status().isOk(), jsonPath("$.completed").value(true) 검증
		// TODO: 한 번 더 호출해서 다시 false로 돌아오는지도 확인하면 더 좋음
	}

	@Test
	void 캐릭터를_삭제하면_DB에서도_실제로_사라진다() throws Exception {
		// TODO: 게임 + 캐릭터 준비
		// TODO: delete("/characters/{characterId}", characterId) 호출 -> status().isNoContent()
		// TODO: assertThat(characterRepository.existsById(characterId)).isFalse() 검증
	}

	@Test
	void 게임을_삭제하면_소속된_캐릭터도_함께_연쇄_삭제된다() throws Exception {
		// 삭제 정책 (2026-07-31 결정): Game 삭제 시 소속 Character 전부 연쇄 삭제.
		// Game.java에 @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true) characters
		// 필드를 추가하기 전(TODO 상태)까지는 이 테스트는 실패한다 - FK 제약조건 위반(500)이 예상됨.
		// Game.java의 TODO를 채운 뒤에 이 테스트가 통과하는지로 구현 완료 여부를 확인하면 됨.
		//
		// TODO: gameRepository.save(new Game(...))로 게임 준비
		// TODO: characterRepository.save(new Character(game, name))로 캐릭터 1개 이상 준비
		// TODO: mockMvc.perform(delete("/games/{gameId}", gameId)) 호출 -> status().isNoContent() 검증
		// TODO: assertThat(gameRepository.existsById(gameId)).isFalse() 검증
		// TODO: assertThat(characterRepository.existsById(characterId)).isFalse() 검증 (캐릭터도 같이 사라졌는지)
	}

}
