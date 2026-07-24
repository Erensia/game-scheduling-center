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
| `party.slots[]` (characterId 배열) | `PartySlot` 테이블 | 배열 인덱스를 `slotIndex`로, 값은 새로 발급된 `Character.id`로 치환 |
| `template.items[].text` | `TemplateItem.text` | 그대로 이관 (완료 상태 없음) |

## 이관 절차 (제안)

1. 사용자가 "백업 다운로드" 버튼으로 받은 JSON 파일을 준비
2. (MVP) 1회성 이관 스크립트 또는 `/migration/import` API로 JSON을 업로드
3. 서버에서 게임 → 캐릭터 → 체크리스트 순으로 생성하며, 원본 uid ↔ 새 PK 매핑을 메모리에 유지
4. 파티는 캐릭터가 모두 생성된 후 마지막에 처리 (슬롯이 캐릭터를 참조하므로)
5. 이관 완료 후 결과 요약(게임 N개, 캐릭터 N개 등)을 사용자에게 보여줌

## 이관 시 주의사항

- 문자열 uid는 시간 기반이라 정렬은 가능하지만 유일성만 보장하면 되므로, 새 PK로 바뀌어도 데이터 정합성엔 문제없음
- `charFilter`(전체/진행중/완료 필터 선택 상태)는 UI 임시 상태라 이관 대상에서 제외
- `selectedGameId`도 UI 임시 상태라 이관 대상에서 제외

## TBD

- [ ] 이관을 API(`/migration/import`)로 제공할지, 개발자가 직접 실행하는 스크립트로만 둘지
- [ ] 이관 중 일부 실패 시 롤백 정책 (전체 롤백 vs 부분 성공 허용)
