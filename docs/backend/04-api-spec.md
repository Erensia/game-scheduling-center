# 04. API 명세서 (초안)

> ERD(`03-erd.md`)가 아직 확정 전이라, 이 문서는 **엔드포인트 목록과 역할**을 먼저 잡는 단계다.
> 요청/응답 상세 스키마(JSON 필드)는 ERD 확정 후 엔드포인트별로 채운다.

## 공통 규칙 (제안)

- Base path: `/api/v1`
- 응답 포맷: JSON, 에러는 `{ "error": "메시지" }` 형태로 통일
- 날짜: ISO-8601 (`yyyy-MM-dd`)
- 인증: MVP 단계에서는 없음 (2단계에서 추가 검토)

## 게임 (Game)

| Method | Path | 설명 |
|---|---|---|
| GET | `/games` | 전체 게임 목록 조회 |
| POST | `/games` | 게임 생성 (`name`, `resetDay`) |
| PATCH | `/games/{gameId}` | 게임 수정 (이름, 리셋 요일) |
| DELETE | `/games/{gameId}` | 게임 삭제 (캐릭터/주간컨텐츠/템플릿/파티 함께 삭제) |

## 캐릭터 (Character)

| Method | Path | 설명 |
|---|---|---|
| GET | `/games/{gameId}/characters` | 게임의 캐릭터 목록 (필터: `?completed=true/false`) |
| POST | `/games/{gameId}/characters` | 캐릭터 생성 (템플릿 적용 시 `templateId` 포함 가능) |
| PATCH | `/characters/{characterId}` | 캐릭터 수정 (이름, `completed` 토글) |
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
| PATCH | `/weekly/{weeklyId}/toggle` | 이번 주 완료 상태 토글 |
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
| POST | `/games/{gameId}/parties` | 파티 생성 |
| PATCH | `/parties/{partyId}` | 파티 이름 변경 |
| PATCH | `/parties/{partyId}/slots/{slotIndex}` | 슬롯에 캐릭터 배정/해제 (`characterId` 또는 null) |
| DELETE | `/parties/{partyId}` | 파티 삭제 |

## 홈 대시보드 집계

| Method | Path | 설명 |
|---|---|---|
| GET | `/dashboard` | 전체 게임 기준 요약 통계 + 게임별 미완료 주간 컨텐츠/캐릭터 목록 (더보기 페이지네이션을 위해 `limit`/`offset` 또는 `page` 쿼리 지원 검토) |

## 마이그레이션 (선택)

| Method | Path | 설명 |
|---|---|---|
| POST | `/migration/import` | 기존 백업 JSON 구조를 받아 초기 데이터로 일괄 생성 (1회성, 05-migration-plan.md 참고) |

## TBD (ERD 확정 후 채울 항목)

- [ ] 각 엔드포인트의 요청/응답 JSON 스키마 상세
- [ ] 에러 응답 코드 체계 (404/400 등 케이스별 정리)
- [ ] `/dashboard`의 더보기 페이지네이션을 서버에서 처리할지, 프론트엔드에서 전체를 받아 처리할지 (지금처럼 데이터量이 적으면 후자가 더 단순할 수 있음)
- [ ] 대량 순서 변경(체크리스트 항목 재정렬 등) API 필요 여부
