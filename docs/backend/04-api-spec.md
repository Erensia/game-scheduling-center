# 04. API 명세서 (초안)

> ERD(`03-erd.md`)가 아직 확정 전이라, 이 문서는 **엔드포인트 목록과 역할**을 먼저 잡는 단계다.
> 요청/응답 상세 스키마(JSON 필드)는 ERD 확정 후 엔드포인트별로 채운다.

## 공통 규칙 (제안)

- Base path: `/api/v1`
- 응답 포맷: JSON. 에러는 아래 "에러 코드 체계" 참고
- 날짜: ISO-8601 (`yyyy-MM-dd`)
- 인증: MVP 단계에서는 없음 (2단계에서 추가 검토)

## 게임 (Game)

| Method | Path | 설명 |
|---|---|---|
| GET | `/games` | 전체 게임 목록 조회 |
| POST | `/games` | 게임 생성 (`name`, `resetDay`, `partySize`) |
| PATCH | `/games/{gameId}` | 게임 수정 (리셋 요일, `partySize`) — 이름 수정은 현재 프론트엔드에 없는 기능이라 MVP 범위에서 제외 |
| DELETE | `/games/{gameId}` | 게임 삭제 (캐릭터/주간컨텐츠/템플릿/파티 함께 삭제) |

## 캐릭터 (Character)

| Method | Path | 설명 |
|---|---|---|
| GET | `/games/{gameId}/characters` | 게임의 캐릭터 목록 (필터: `?completed=true/false`) |
| POST | `/games/{gameId}/characters` | 캐릭터 생성 (템플릿 적용 시 `templateId` 포함 가능) |
| PATCH | `/characters/{characterId}` | 캐릭터의 `completed` 토글 — 이름 수정은 현재 프론트엔드에 없는 기능이라 MVP 범위에서 제외 |
| DELETE | `/characters/{characterId}` | 캐릭터 삭제 |

## 체크리스트 항목 (ChecklistItem)

| Method | Path | 설명 |
|---|---|---|
| POST | `/characters/{characterId}/items` | 항목 추가 (`text`) |
| PATCH | `/items/{itemId}` | 항목 완료 토글 |
| DELETE | `/items/{itemId}` | 항목 삭제 |

## 주간 컨텐츠 (WeeklyContent)

| Method | Path | 설명 |
|---|---|---|
| GET | `/games/{gameId}/weekly` | 게임의 주간 컨텐츠 목록 (응답에 `completedThisWeek` 계산값 포함) |
| POST | `/games/{gameId}/weekly` | 주간 컨텐츠 추가 (`name`) |
| PATCH | `/weekly/{weeklyId}/toggle` | 완료 상태를 토글 (체크/언체크를 하나의 엔드포인트로 처리). 완료 처리 시 `lastCompletedWeekStart`를 이번 주 시작일로 기록, 해제 시 `null`로 되돌림. 현재 프론트엔드의 `doneWeekKey` 체크박스 change 이벤트와 1:1 대응 |
| DELETE | `/weekly/{weeklyId}` | 주간 컨텐츠 삭제 |

## 템플릿 (CharacterTemplate)

| Method | Path | 설명 |
|---|---|---|
| GET | `/games/{gameId}/templates` | 게임의 템플릿 목록 |
| POST | `/games/{gameId}/templates` | 템플릿 저장 (`name`, `items: [{text}]`) — 캐릭터에서 "템플릿으로 저장" |
| DELETE | `/templates/{templateId}` | 템플릿 삭제 |

## 파티 (Party)

| Method | Path | 설명 |
|---|---|---|
| GET | `/games/{gameId}/parties` | 게임의 파티 목록 |
| POST | `/games/{gameId}/parties` | 파티 생성 (`name`) — 슬롯 개수는 요청에 포함하지 않고, **생성 시점의 게임 `partySize`를 파티 자신의 값으로 복사(스냅샷)** 해 그만큼 서버가 슬롯을 자동 생성. 이후 게임의 `partySize`가 바뀌어도 이 파티는 영향받지 않음 |
| PATCH | `/parties/{partyId}` | 파티 이름 변경 |
| PATCH | `/parties/{partyId}/slots/{slotIndex}` | 슬롯에 캐릭터 배정/해제 (`characterId` 또는 null) |
| DELETE | `/parties/{partyId}` | 파티 삭제 |

## 홈 대시보드 집계

| Method | Path | 설명 |
|---|---|---|
| GET | `/dashboard` | 전체 게임 기준 요약 통계 + 게임별 미완료 주간 컨텐츠/캐릭터 목록. **더보기 페이지네이션은 서버에서 처리** (`page`/`size` 쿼리 파라미터로 게임별 그룹 목록을 페이지 단위로 반환) |

## 마이그레이션

초기 데이터 이관은 API로 제공하지 않는다 (결정: `01-requirements.md` 6절). 1회성 스크립트로 처리하며, 자세한 절차는 `05-migration-plan.md` 참고.

## 에러 코드 체계

### 응답 포맷

```json
{
  "code": "GAME_NOT_FOUND",
  "message": "해당 게임을 찾을 수 없습니다.",
  "path": "/api/v1/games/123",
  "timestamp": "2026-07-24T10:00:00"
}
```

검증 실패처럼 필드 단위 오류가 여러 개 발생할 수 있는 경우, `message` 대신 `errors` 배열로 필드별 메시지를 내려준다.

```json
{
  "code": "VALIDATION_ERROR",
  "errors": [
    { "field": "resetDay", "message": "0에서 6 사이여야 합니다." },
    { "field": "partySize", "message": "1 이상이어야 합니다." }
  ],
  "path": "/api/v1/games",
  "timestamp": "2026-07-24T10:00:00"
}
```

### 상황별 매핑

| HTTP 상태 | 코드 | 상황 | 예시 |
|---|---|---|---|
| 400 | `VALIDATION_ERROR` | 요청 필드 값이 유효하지 않음 | 이름 공백, `resetDay` 0~6 범위 밖, `partySize` ≤ 0 |
| 400 | `INVALID_SLOT_INDEX` | 파티 슬롯 인덱스가 그 파티의 `partySize`(생성 시점 스냅샷) 범위를 벗어남 | 파티의 `partySize=3`인데 `slotIndex=5` 요청 |
| 400 | `CROSS_GAME_REFERENCE` | 다른 게임 소속 리소스를 잘못 참조 | A게임 캐릭터 생성 시 B게임의 `templateId` 적용 시도 |
| 404 | `GAME_NOT_FOUND` / `CHARACTER_NOT_FOUND` / `WEEKLY_CONTENT_NOT_FOUND` / `TEMPLATE_NOT_FOUND` / `PARTY_NOT_FOUND` / `ITEM_NOT_FOUND` | 요청한 리소스 ID가 존재하지 않음 | 없는 ID로 조회·수정·삭제 요청 |
| 409 | *(MVP 보류)* | 동시 편집 충돌 | `07-deployment.md`에서 배포 방식·동시 편집 정책 확정 후 코드 추가 |
| 500 | `INTERNAL_ERROR` | 예상하지 못한 서버 오류 | 클라이언트에는 상세 스택트레이스 노출하지 않고 서버 로그에만 기록 |

### 처리 아키텍처 (Spring Boot)

- **프레임워크 검증(400 대부분)**: 요청 DTO에 Bean Validation 애너테이션(`@NotBlank`, `@Min`, `@Max` 등)을 붙이고 컨트롤러에서 `@Valid`로 받으면, `MethodArgumentNotValidException`을 스프링이 자동으로 던진다. 이걸 잡아서 `VALIDATION_ERROR` 포맷으로 변환.
- **도메인 규칙 검증(`INVALID_SLOT_INDEX`, `CROSS_GAME_REFERENCE` 등)**: 서비스 레이어에서 직접 커스텀 예외(`InvalidSlotIndexException`, `CrossGameReferenceException` 등, 공통 상위 클래스 `BadRequestException` 상속)를 던진다.
- **리소스 없음(404)**: 리포지토리 조회 결과가 없을 때 `ResourceNotFoundException(resourceType, id)` 형태의 공통 예외를 던지고, 메시지/코드는 `resourceType`으로 조립 (예: `GAME_NOT_FOUND`).
- **한 곳에서 모아 처리**: `@RestControllerAdvice` 클래스 하나에 `@ExceptionHandler`들을 모아, 각 예외 타입 → HTTP 상태 코드 → 위 응답 포맷으로 일괄 변환. 컨트롤러/서비스 코드에는 try-catch가 거의 없어도 된다.
- **예외 하나도 안 잡히는 경우(500)**: `@ExceptionHandler(Exception.class)`로 잡아 `INTERNAL_ERROR`로 감싸고, 실제 원인은 서버 로그에만 남긴다 (클라이언트에 스택트레이스 노출 금지).

## TBD (ERD 확정 후 채울 항목)

- [ ] 각 엔드포인트의 요청/응답 JSON 스키마 상세
- [x] ~~에러 응답 코드 체계 (404/400 등 케이스별 정리)~~ → **결정: 위 "에러 코드 체계" 절 참고**
- [x] ~~`/dashboard`의 더보기 페이지네이션을 서버에서 처리할지, 프론트엔드에서 전체를 받아 처리할지~~ → **결정: 서버에서 처리** (데이터 증가 대비, `01-requirements.md` 7절 참고)
- [x] ~~대량 순서 변경(체크리스트 항목 재정렬 등) API 필요 여부~~ → **결정: 지원하지 않음** (현재 프론트엔드에 재정렬 기능 없음, `id` 오름차순 정렬로 충분. `03-erd.md` 참고)
- [x] ~~게임의 `partySize`를 수정(`PATCH /games/{gameId}`)했을 때, 이미 생성된 파티들의 슬롯 개수를 어떻게 처리할지~~ → **결정: `Party`가 생성 시점의 `partySize`를 스냅샷으로 저장**해 문제 자체를 없앰 (`03-erd.md`, `01-requirements.md` 7절 참고). 기존 파티는 영향받지 않고, 새로 만드는 파티부터만 바뀐 값을 따름
