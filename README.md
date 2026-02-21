# Back-likelion

Spring Boot 기반 커뮤니티 플랫폼 백엔드 API 서버

## Tech Stack

- **Java** 17
- **Spring Boot** 3.3.1
- **Spring Data JPA** + H2 Database
- **Spring Security** + JWT (com.auth0:java-jwt)
- **SpringDoc OpenAPI** (Swagger UI)
- **Lombok**
- **Gradle** 8.8

## Project Structure

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

## Domain Model

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

## API Endpoints

### Member (`/api`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/signUp` | 회원가입 |
| GET | `/api/member` | 내 정보 조회 |

### Post (`/api/post`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/post` | 게시글 작성 |
| GET | `/api/post/{id}` | 게시글 조회 |
| PUT | `/api/post/{id}` | 게시글 수정 (작성 당일만 가능) |
| DELETE | `/api/post/{id}` | 게시글 삭제 |
| POST | `/api/post/{id}/like` | 좋아요 |
| DELETE | `/api/post/{id}/like` | 좋아요 취소 |

### Comment (`/api`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/post/{id}/comment/{comment_id}` | 댓글/대댓글 작성 (comment_id=0이면 댓글) |
| GET | `/api/comment/{id}` | 댓글 상세 조회 |
| PUT | `/api/comment/{id}` | 댓글 수정 |
| DELETE | `/api/comment/{id}` | 댓글 삭제 |

### Meeting (`/api/meeting`)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/meeting` | 모임 목록 조회 (페이지네이션, 카테고리 필터) |
| POST | `/api/meeting` | 모임 생성 |
| GET | `/api/meeting/{meetingId}` | 모임 상세 조회 |
| POST | `/api/meeting/{meetingId}` | 모임 참여 |

### Authentication

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/login` | 로그인 (JSON: email, password) |

## Authentication

JWT 기반 Stateless 인증 방식을 사용합니다.

- **로그인 성공 시**: `Authorization` (Access Token), `Authorization-refresh` (Refresh Token) 헤더로 토큰 발급
- **인증이 필요한 요청**: `Authorization: Bearer {accessToken}` 헤더 포함
- **공개 엔드포인트**: `/api/login`, `/api/signUp`, `/swagger-ui/*`

## Getting Started

### Prerequisites

- Java 17+
- Gradle 8.x

### Configuration

`src/main/resources/application.properties` 또는 `application.yml`에 아래 설정을 추가하세요:

```properties
spring.application.name=backend

# H2 Database
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb

# JWT
jwt.secret-key=your-secret-key
jwt.access.expiration=3600
jwt.access.header=Authorization
jwt.refresh.expiration=1209600
jwt.refresh.header=Authorization-refresh
```

### Build & Run

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

### API Documentation

서버 실행 후 Swagger UI에서 API를 확인할 수 있습니다:

```
http://localhost:8080/swagger-ui/index.html
```

## Error Response Format

모든 에러 응답은 통일된 형식으로 반환됩니다:

```json
{
  "status": 404,
  "code": "POS-201",
  "message": "게시글을 찾을 수 없습니다."
}
```

### Error Code Convention

| Prefix | Domain | Example |
|--------|--------|---------|
| `AUTH` | 인증 | AUTH-001 (로그인 필요) |
| `CMT` | 댓글 | CMT-201 (댓글 없음) |
| `POS` | 게시글 | POS-201 (게시글 없음) |
| `LIK` | 좋아요 | LIK-301 (중복 좋아요) |
| `MTG` | 모임 | MTG-201 (모임 없음) |
| `MBR` | 회원 | MBR-301 (중복 회원) |
