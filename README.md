# Back-likelion

Spring Boot 기반 커뮤니티 플랫폼 백엔드 API 서버

## 기술 스택

- **Java** 17
- **Spring Boot** 3.3.1
- **Spring Data JPA** + H2 Database (인메모리)
- **Spring Security** + JWT (com.auth0:java-jwt)
- **SpringDoc OpenAPI** (Swagger UI)
- **Lombok**
- **Gradle** 8.8

## 프로젝트 구조

```
src/main/java/backend/backend/
├── configuration/         # Security, Swagger 설정
├── controller/            # REST API 컨트롤러
├── service/               # 비즈니스 로직
├── repository/            # JPA Repository
├── domain/
│   ├── common/            # BaseEntity, BusinessException, ResponseCode
│   ├── enums/             # ActiveStatus, MeetingCategory, Week
│   ├── mapping/           # MeetingMember (N:M 조인 테이블)
│   └── dto/               # 요청/응답 DTO
└── global/
    ├── jwt/               # JWT 토큰 생성/검증
    ├── login/             # 인증 필터 및 핸들러
    ├── exception/         # 전역 예외 처리
    └── util/              # SecurityUtil
```

## 도메인 모델

```
Member ──┬── 1:N ──→ Post
         ├── 1:N ──→ Comment
         ├── 1:N ──→ Likes
         └── N:M ──→ Meeting (via MeetingMember)

Post ────┬── 1:N ──→ Comment
         └── 1:N ──→ Likes

Comment ─── 1:N ──→ Comment (대댓글, self-referencing)
```

- **Soft Delete**: Post, Comment는 `activeStatus` 필드로 논리 삭제
- **JPA Auditing**: 모든 엔티티에 `createdAt`, `updatedAt` 자동 기록

## API 엔드포인트

### 인증

| Method | Path | 설명 |
|--------|------|------|
| POST | `/login` | 로그인 (JSON: `username`, `password`) |

> 로그인 성공 시 응답 **헤더**에 토큰이 포함됩니다:
> - `Authorization`: Access Token
> - `Authorization-refresh`: Refresh Token

### 회원 (`/api`)

| Method | Path | 설명 |
|--------|------|------|
| POST | `/api/signUp` | 회원가입 (email, password, nickName, emoji) |
| GET | `/api/member` | 내 정보 조회 |

### 게시글 (`/api/post`)

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/post` | 게시글 목록 조회 (페이지네이션) |
| POST | `/api/post` | 게시글 작성 |
| GET | `/api/post/{id}` | 게시글 상세 조회 (댓글, 좋아요 수 포함) |
| PUT | `/api/post/{id}` | 게시글 수정 (작성 당일만 가능) |
| DELETE | `/api/post/{id}` | 게시글 삭제 |
| POST | `/api/post/{id}/like` | 좋아요 (JWT 기반) |
| DELETE | `/api/post/{id}/like` | 좋아요 취소 (JWT 기반) |

### 댓글 (`/api`)

| Method | Path | 설명 |
|--------|------|------|
| POST | `/api/post/{id}/comment/{comment_id}` | 댓글/대댓글 작성 (`comment_id=0`이면 댓글) |
| GET | `/api/comment/{id}` | 댓글 상세 조회 |
| PUT | `/api/comment/{id}` | 댓글 수정 |
| DELETE | `/api/comment/{id}` | 댓글 삭제 |

### 모임 (`/api/meeting`)

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/meeting` | 모임 목록 조회 (페이지네이션, 카테고리 필터) |
| POST | `/api/meeting` | 모임 생성 |
| GET | `/api/meeting/{meetingId}` | 모임 상세 조회 |
| POST | `/api/meeting/{meetingId}` | 모임 참여 |

## 인증 방식

JWT 기반 Stateless 인증 방식을 사용합니다.

- **로그인**: `POST /login`에 `{ "username": "이메일", "password": "비밀번호" }` 전송
- **토큰 발급**: 로그인 성공 시 응답 헤더로 Access Token, Refresh Token 발급
- **인증 요청**: `Authorization: Bearer {accessToken}` 헤더 포함
- **토큰 갱신**: Access Token 만료 시 `Authorization-refresh` 헤더로 Refresh Token 전송하면 자동 갱신
- **공개 엔드포인트**: `/login`, `/api/signUp`, `/swagger-ui/*`

## 시작하기

### 사전 요구사항

- Java 17+
- Gradle 8.x

### 설정

`src/main/resources/application.properties`:

```properties
spring.application.name=backend

# H2 Database
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create

# JWT
jwt.secret-key=your-secret-key-at-least-512-bits
jwt.access.expiration=3600
jwt.access.header=Authorization
jwt.refresh.expiration=1209600
jwt.refresh.header=Authorization-refresh
```

### 빌드 및 실행

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

서버는 `http://localhost:8080`에서 실행됩니다.

### API 문서

서버 실행 후 Swagger UI에서 API를 확인할 수 있습니다:

```
http://localhost:8080/swagger-ui/index.html
```

## 에러 응답 형식

모든 에러 응답은 통일된 형식으로 반환됩니다:

```json
{
  "status": 404,
  "code": "POS-201",
  "message": "게시글을 찾을 수 없습니다."
}
```

### 에러 코드 규칙

| Prefix | 도메인 | 예시 |
|--------|--------|------|
| `AUTH` | 인증 | AUTH-001 (로그인 필요) |
| `MBR` | 회원 | MBR-301 (중복 회원) |
| `POS` | 게시글 | POS-201 (게시글 없음) |
| `CMT` | 댓글 | CMT-201 (댓글 없음) |
| `LIK` | 좋아요 | LIK-301 (중복 좋아요) |
| `MTG` | 모임 | MTG-201 (모임 없음) |

## CORS 설정

프론트엔드(`http://localhost:3000`)와의 통신을 위해 CORS가 설정되어 있습니다:

- 허용 메서드: GET, POST, PUT, DELETE, OPTIONS
- 노출 헤더: Authorization, Authorization-refresh
- 인증 정보: 허용
