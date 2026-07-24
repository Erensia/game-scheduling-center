# Game Scheduling Center API (백엔드)

`docs/backend/`에서 설계한 문서(요구사항, ERD, API 명세, 기술 스택)를 기반으로 한
Spring Boot 백엔드 프로젝트입니다. 프론트엔드(`../index.html` 등)는 그대로 두고,
데이터 계층을 `localStorage`에서 이 API로 교체하는 것이 목표입니다.

## 기술 스택 (`docs/backend/06-tech-stack.md` 결정 사항)

| 항목 | 값 |
|---|---|
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 4.1.0 (Spring Framework 7) |
| 빌드 도구 | Gradle |
| DB | PostgreSQL (개발/운영 동일 엔진, 인스턴스만 분리) |
| ORM | Spring Data JPA |
| API 문서 | springdoc-openapi (Swagger UI) |

## 사전 준비물

1. **JDK 21** 설치 확인
   ```bash
   java -version
   ```
2. **PostgreSQL** 설치 및 실행 (로컬 설치 또는 Docker 둘 다 가능)
   ```bash
   # Docker를 쓰는 경우 예시
   docker run --name game-scheduling-db -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres
   ```
3. 개발용 데이터베이스 생성 (DB 이름은 `application-dev.yml`과 일치해야 함)
   ```bash
   psql -U postgres -c "CREATE DATABASE game_scheduling_dev;"
   ```
   접속 정보(사용자명/비밀번호)가 로컬 환경과 다르면 `src/main/resources/application-dev.yml`을
   직접 수정하세요 (이 파일은 로컬 개발 전용이라 커밋해도 무방합니다).

## Gradle Wrapper에 대한 안내

이 스캐폴딩에는 `build.gradle`/`settings.gradle`만 포함되어 있고, **Gradle Wrapper(`gradlew`, `gradlew.bat`, `gradle-wrapper.jar`)는 아직 생성되지 않았습니다.**
(작업 환경에 Gradle이 설치되어 있지 않고 외부 저장소 접근도 제한돼 있어 여기서는 만들 수 없었습니다.)

**IntelliJ IDEA로 열면 가장 간단합니다** — `backend` 폴더를 Gradle 프로젝트로 열면 IntelliJ에 내장된 Gradle로 자동 동기화됩니다. 이후 팀/다른 컴퓨터에서도 동일한 Gradle 버전으로 빌드되게 하려면, IntelliJ의 Gradle 탭에서 "Add Gradle Wrapper" 액션을 실행하거나, 로컬에 Gradle이 설치되어 있다면 아래 명령으로 생성해서 커밋하세요.

```bash
gradle wrapper --gradle-version 8.14
```

## 실행 방법

IntelliJ에서 `GameSchedulingApplication`을 실행하거나, wrapper 생성 후:

```bash
./gradlew bootRun
```

정상적으로 뜨면 아래로 접속해 환경이 제대로 준비됐는지 확인하세요.

- 헬스체크: http://localhost:8080/api/v1/health → `{"status":"OK"}`
- API 문서(Swagger UI): http://localhost:8080/api/v1/swagger-ui.html

## 프로파일

- `dev` (기본값): 로컬 PostgreSQL, 엔티티 변경사항 자동 반영(`ddl-auto: update`), SQL 로그 출력
- `prod`: 환경변수(`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`)로 접속 정보 주입, 스키마 자동 변경 없음, API 문서 비공개

## 패키지 구조 (현재)

```
com.erensia.gamescheduling
├── GameSchedulingApplication.java   — 진입점, JPA Auditing 활성화
└── common/
    ├── BaseEntity.java              — 전 엔티티 공통 (id, createdAt)
    └── HealthController.java        — 환경 확인용 임시 엔드포인트
```

게임/캐릭터/체크리스트/주간컨텐츠/템플릿/파티 등 실제 도메인 코드는
`docs/backend/03-erd.md`, `04-api-spec.md`를 기준으로 다음 단계에서 추가합니다.
