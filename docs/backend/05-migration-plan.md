# 05. 마이그레이션 계획

## 목적

기존 사용자가 브라우저 `localStorage`에 이미 쌓아둔 게임/캐릭터/체크리스트/주간컨텐츠/템플릿/파티 데이터를,
새 백엔드로 옮길 때 유실 없이 이관하기 위한 계획.

## 현재 데이터 구조 (기준: `app.js`의 `STORAGE_KEY`, "백업 다운로드" JSON)

```json
{
  "games": [
    {
      "id": "string (uid)",
      "name": "string",
      "resetDay": 0,
      "charFilter": "all",
      "characters": [
        {
          "id": "string (uid)",
          "name": "string",
          "completed": false,
          "items": [
            { "id": "string (uid)", "text": "string", "done": false }
          ]
        }
      ],
      "weekly": [
        { "id": "string (uid)", "name": "string", "doneWeekKey": "2026-07-20 또는 null" }
      ],
      "templates": [
        {
          "id": "string (uid)",
          "name": "string",
          "items": [ { "text": "string" } ]
        }
      ],
      "parties": [
        {
          "id": "string (uid)",
          "name": "string",
          "slots": ["characterId 또는 null", "..."]
        }
      ]
    }
  ],
  "selectedGameId": "string 또는 null"
}
```

## 매핑 규칙

| 원본 필드 | 대상 | 변환 규칙 |
|---|---|---|
| `game.id` (문자열 uid) | `Game.id` (Long) | 새 DB PK 발급, 원본 uid는 캐릭터/파티의 참조를 그대로 잇기 위해 **이관 스크립트 내부에서만** 매핑 테이블로 사용 (uid → 새 PK) |
| `character.items[].id` | 폐기 후 재발급 | 프론트엔드가 uid를 다시 참조하는 곳이 없으므로 새 PK로 재발급해도 무방 |
| `weekly.doneWeekKey` | `WeeklyContent.lastCompletedWeekStart` | 문자열 날짜(`yyyy-MM-dd`) 그대로 파싱, `null`이면 미완료로 저장 |
| (원본에 없음) | `Game.partySize` | 백업 JSON에는 없는 필드. 이관 담당자가 게임별 실제 인원수를 확인해 스크립트 설정으로 직접 입력 (아래 이관 절차 2번 참고) |
| (원본에 없음) | `Party.partySize` | 이관 시점의 `Game.partySize` 설정값을 그대로 복사해 각 파티의 스냅샷으로 저장 (`01-requirements.md` 7절의 스냅샷 결정 참고) |
| `party.slots[]` (characterId 배열) | `PartySlot` 테이블 | 배열 인덱스를 `slotIndex`로, 값은 새로 발급된 `Character.id`로 치환. 원본은 4칸 고정이었으므로 새 `partySize`와 개수가 다를 수 있음 — 빈 슬롯 우선 제거, 잘려나간 캐릭터는 로그로 기록 (이관 절차 5번 참고) |
| `template.items[].text` | `TemplateItem.text` | 그대로 이관 (완료 상태 없음) |

## 이관 절차 (확정: 스크립트 방식)

1. 사용자가 "백업 다운로드" 버튼으로 받은 JSON 파일을 준비
2. **게임별 정확한 `partySize` 값을 스크립트 설정(예: 게임 이름 → 인원수 매핑)으로 미리 입력** — 기존 백업 JSON에는 `partySize` 개념이 없고, 구 프론트엔드가 파티 슬롯을 4개로 하드코딩해온 탓에 실제 값(명조·젠존제=3명 등)을 데이터에서 자동으로 유추할 수 없기 때문. 사람이 직접 확인하고 입력해야 한다.
   **JSON에 등장하는 게임 중 하나라도 설정에 `partySize`가 빠져 있으면, 스크립트는 기본값을 쓰지 않고 그 즉시 에러를 내며 중단한다** (조용히 잘못된 값으로 진행되는 것을 막기 위함).
3. 백엔드 초기 구축 시 1회성 이관 스크립트(CLI 또는 `CommandLineRunner` 등)를 실행해 JSON을 읽어들임
4. 스크립트에서 게임(+ 2번의 `partySize`) → 캐릭터 → 체크리스트 순으로 생성하며, 원본 uid ↔ 새 PK 매핑을 메모리에 유지
5. 파티는 캐릭터가 모두 생성된 후 마지막에 처리 — 게임의 `partySize` 값을 그 파티의 `partySize` 스냅샷으로 저장하고, 그 값만큼 `PartySlot`을 생성해 원본 `slots` 배열 값을 매핑한다.
   - 원본 슬롯 수(4)가 새 `partySize`보다 **적으면** 나머지는 빈 슬롯으로 채운다.
   - **많으면**, 빈 슬롯(`null`)부터 우선적으로 잘라내고 캐릭터가 채워진 슬롯은 최대한 유지한다. 즉 단순히 배열 순서대로 자르지 않는다 — 앞쪽이 비어있고 뒤쪽에 캐릭터가 채워진 경우, 배열 순서대로 자르면 채워진 슬롯이 유실될 수 있기 때문이다.
   - 그럼에도 채워진 슬롯 수가 `partySize`를 초과해 불가피하게 캐릭터가 잘리는 경우, **어떤 파티에서 어떤 캐릭터가 잘렸는지 반드시 로그로 남긴다.** (조용히 데이터가 사라지는 것을 방지)
6. 이관 완료 후 결과 요약(게임 N개, 캐릭터 N개, 잘려나간 파티 슬롯 목록 등)을 콘솔 로그로 출력

> 영구 API 엔드포인트(`/migration/import` 등)로 제공하지 않기로 결정했다 (`01-requirements.md` 6절, `04-api-spec.md` 참고) — 1회성 작업을 위해 지속적으로 유지·검증해야 하는 API를 만드는 것은 과한 설계라고 판단.

## 이관 시 주의사항

- 문자열 uid는 시간 기반이라 정렬은 가능하지만 유일성만 보장하면 되므로, 새 PK로 바뀌어도 데이터 정합성엔 문제없음
- `charFilter`(전체/진행중/완료 필터 선택 상태)는 UI 임시 상태라 이관 대상에서 제외
- `selectedGameId`도 UI 임시 상태라 이관 대상에서 제외

## TBD

- [x] ~~이관을 API로 제공할지, 개발자가 직접 실행하는 스크립트로만 둘지~~ → **결정: 스크립트 방식**
- [ ] 이관 중 일부 실패 시 롤백 정책 (전체 롤백 vs 부분 성공 허용)
