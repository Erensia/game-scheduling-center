# 02. 도메인 용어 정리

> 게임마다 육성 요소를 부르는 이름이 다르다 (명조: 공명체인·에코, 젠존제: 디스크 드라이브·W-엔진 등).
> 이런 게임별 고유 용어는 구조적 엔티티명으로 고정하지 않고, 자유 텍스트 필드(`ChecklistItem.text`)로 남긴다.
> 아래는 게임에 관계없이 공통으로 쓰이는 **구조적 도메인 용어**만 정리한다.

## 엔티티 용어

| 한글 명칭 | 영문 명칭(엔티티/클래스) | 설명 |
|---|---|---|
| 게임 | `Game` | 사용자가 등록하는 게임 카테고리 (예: 명조, 젠존제) |
| 캐릭터 | `Character` | 게임에 속한 육성 대상 캐릭터 |
| 체크리스트 항목 | `ChecklistItem` | 캐릭터별 자유 텍스트 육성 항목 (게임별 고유 용어가 여기에 자유롭게 들어감) |
| 주간 컨텐츠 | `WeeklyContent` | 게임에 속한, 매주 리셋되는 완료 대상 컨텐츠 |
| 템플릿 | `CharacterTemplate` | 체크리스트 구성을 게임 단위로 저장해둔 재사용 세트 |
| 파티 | `Party` | 게임에 속한, 여러 캐릭터를 슬롯에 배정하는 구성 |
| 파티 슬롯 | `PartySlot` | 파티 내 캐릭터 하나를 배정하는 자리 (비어있을 수 있음) |

## 필드 용어 및 현재 프론트엔드 대응

### Game
| 필드 (현재 JS) | 제안 필드명 | 타입 | 비고 |
|---|---|---|---|
| `id` | `id` | Long (DB에서 재발급) | 프론트 uid() 문자열 → DB PK로 전환 |
| `name` | `name` | String | 예: "명조" |
| `resetDay` | `resetDay` | Integer (0~6) | 0=일요일 ~ 6=토요일, 주간 리셋 기준 요일 |
| *(신규)* | `partySize` | Integer | 이 게임의 파티 슬롯 개수 (예: 명조·젠존제=3). 파티 생성 시 이 값만큼 슬롯 자동 생성 |

### Character
| 필드 (현재 JS) | 제안 필드명 | 타입 | 비고 |
|---|---|---|---|
| `id` | `id` | Long | |
| `name` | `name` | String | |
| `completed` | `completed` | Boolean | "더 이상 파밍 안 해도 됨" 표시 |
| `items` | `checklistItems` | List\<ChecklistItem\> | 1:N |

### ChecklistItem
| 필드 (현재 JS) | 제안 필드명 | 타입 | 비고 |
|---|---|---|---|
| `text` | `text` | String | 자유 텍스트 (게임별 용어가 여기 들어감) |
| `done` | `done` | Boolean | |

### WeeklyContent
| 필드 (현재 JS) | 제안 필드명 | 타입 | 비고 |
|---|---|---|---|
| `name` | `name` | String | |
| `doneWeekKey` | `lastCompletedWeekStart` | LocalDate (nullable) | 완료 처리된 "주 시작일". 현재 주 시작일과 비교해 완료 여부 판단 |

> 참고: 현재 프론트엔드는 `weekKeyFor(resetDay)`로 "가장 최근 리셋 요일"의 날짜를 계산해 문자열로 비교한다.
> 백엔드에서는 이 계산 로직을 서비스 레이어에 그대로 옮기거나, 매 조회 시 서버에서 계산해 응답에 `completedThisWeek: boolean`으로 내려주는 방식을 검토한다 (03-erd.md, 04-api-spec.md에서 결정).

### CharacterTemplate
| 필드 (현재 JS) | 제안 필드명 | 타입 | 비고 |
|---|---|---|---|
| `name` | `name` | String | |
| `items` | `items` | List\<TemplateItem\> | 저장 시점의 텍스트만 복사 (완료 상태는 없음) |

### Party / PartySlot
| 필드 (현재 JS) | 제안 필드명 | 타입 | 비고 |
|---|---|---|---|
| `name` | `name` | String | |
| *(신규)* | `partySize` | Integer | 파티 생성 시점의 `Game.partySize`를 복사한 스냅샷. 이후 게임의 `partySize`가 바뀌어도 이 값은 그대로 유지됨 |
| `slots` | `slots` | List\<PartySlot\> | 파티 생성 시 자신의 `partySize`(스냅샷)만큼 슬롯이 자동 생성됨 |

## 상태값 계산 용어

| 용어 | 정의 |
|---|---|
| 육성 필요 (pending) | `character.completed === false` |
| 이번 주 남은 컨텐츠 | `weeklyContent.lastCompletedWeekStart !== 이번_주_시작일` |
| 이번 주 시작일 | 게임의 `resetDay` 기준으로, 오늘 이전(포함) 가장 최근에 도래한 리셋 요일의 날짜 |

## TBD

- [x] ~~`Game`, `Character` 등 PK를 `Long`(auto increment)으로 할지 `UUID`로 할지~~ → **결정: `Long`** (`01-requirements.md` 7절 참고)
- [ ] 다국어(용어 번역) 지원 여부는 범위 밖으로 확정할지 논의 필요
