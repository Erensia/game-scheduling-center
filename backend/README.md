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

IDE 없이 로컬 Gradle이 설치되어 있다면 `backend` 폴더에서 아래 한 줄이면 끝입니다.

```bash
gradle wrapper --gradle-version 9.5.1
```

### STS(Spring Tool Suite)를 쓰는 경우

STS도 Buildship이라는 Gradle 연동 플러그인을 내장하고 있어서, IntelliJ처럼 로컬에 Gradle을 따로 설치하지 않아도 됩니다. 방법은 두 가지예요.

**방법 1 — Gradle Tasks 뷰에서 `wrapper` 태스크 실행 (가장 간단)**
1. `File → Import... → Gradle → Existing Gradle Project` 선택 → `backend` 폴더 지정 → 다음 화면(Import Options)에서:
   - **Gradle distribution**을 `Gradle wrapper`가 아니라 **`Specific Gradle version`으로 선택** — 이 프로젝트엔 아직 wrapper 설정이 없어서 `Gradle wrapper`를 그대로 두면 실패할 수 있음
   - 버전은 드롭다운에 기본으로 뜨는 RC(release candidate) 버전이 아니라 **정식 안정 버전(예: `9.5.1`)을 직접 선택/입력**
   - Java home은 비워두면 STS 기본 JDK 사용, Java 21이 아니라면 직접 경로 지정
   - `Show Console View`를 체크해두면 다운로드/빌드 로그를 실시간으로 볼 수 있어 문제 생겼을 때 원인 파악이 쉬움
   - `Finish`보다는 `Next >`로 프로젝트 구조를 한 번 확인한 뒤 마지막에 `Finish`
   (STS 내장 Buildship이 지정한 Gradle 배포판으로 동기화합니다. 인터넷 연결 필요 — Maven Central에서 의존성을 받아옵니다.)
2. `Window → Show View → Other... → Gradle → Gradle Tasks` 로 Gradle Tasks 뷰를 엽니다.
3. 프로젝트 트리에서 `build setup → wrapper` 더블클릭 (또는 프로젝트 우클릭 → `Run As → Gradle Task...` → `wrapper` 입력)
4. 실행이 끝나면 `backend` 폴더에 `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`가 생성됩니다. 이걸 커밋해두면 이후엔 `./gradlew` 명령으로 누구나 동일한 Gradle 버전으로 빌드할 수 있어요.

**방법 2 — Gradle을 직접 설치 후 터미널에서 실행**
IDE 플러그인 없이 하고 싶다면, [gradle.org](https://gradle.org/releases/)에서 바이너리(zip)를 받아 압축을 풀고 `bin` 폴더를 PATH에 추가한 뒤:
```bash
gradle -v          # 설치 확인
cd backend
gradle wrapper --gradle-version 9.5.1
```
(Windows라면 SDKMAN 대신 zip 압축 해제 후 시스템 환경 변수 PATH에 `...\gradle-9.5.1\bin` 추가하는 방식이 가장 간단해요.)

둘 다 어렵다면, 일단 **wrapper 없이 STS에서 `Run As → Spring Boot App`으로 바로 실행**해도 됩니다 — wrapper는 "빌드 재현성"을 위한 것이지, 지금 당장 실행 자체를 막는 건 아니에요.

### STS Import 단계에서 문제가 생겼다면

`Import... → Gradle → Existing Gradle Project`에서 막히거나 결과를 알 수 없을 때 확인 순서:

1. **결과 확인 방법**
   - `Window → Show View → Other... → General → Progress` — 아직 다운로드가 진행 중인지 확인 (처음엔 5~15분도 걸릴 수 있음, 멈춘 것처럼 보여도 실제로는 진행 중일 수 있음)
   - `Window → Show View → Other... → General → Error Log` — 실패했다면 여기 빨간 에러가 남음
   - Package Explorer에서 `backend` 프로젝트에 빨간 X 표시가 있으면 실패

2. **흔한 원인**
   - **네트워크/방화벽·프록시 차단**: `services.gradle.org`(Gradle 배포판), `repo.maven.apache.org`/`repo1.maven.org`(의존성), `plugins.gradle.org`(플러그인) 중 하나라도 막혀있으면 멈추거나 타임아웃. 회사/기관 네트워크라면 우선 의심
   - **Java 버전**: `build.gradle`이 Java 21을 요구하는데 로컬에 21 미만이거나 없으면, Gradle이 Java 21을 자동으로 받으려다 막힐 수 있음 (`java -version`으로 먼저 확인)
   - 이 셋 다 아니라면 Error Log의 메시지 원문이 원인 파악에 필요함

## 실행 방법

IntelliJ/STS에서 `GameSchedulingApplication`을 실행하거나, wrapper 생성 후:

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
