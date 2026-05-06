# Likelion Backend

Likelion Backend는 게시글, 댓글, 좋아요, 모임 기능을 제공하는 Spring Boot 커뮤니티 플랫폼 API 서버입니다. H2 인메모리 DB와 JWT 인증을 사용해 프론트엔드와 빠르게 연동할 수 있도록 구성되어 있습니다.

## 주요 기능

- 회원가입, 로그인, 내 정보 조회
- JWT Access/Refresh Token 발급과 자동 갱신 흐름
- 게시글 목록, 상세, 작성, 수정, 삭제
- 게시글 좋아요와 좋아요 취소
- 댓글과 대댓글 작성, 조회, 수정, 삭제
- 모임 목록, 상세, 생성, 참여
- 카테고리 기반 모임 필터링
- soft delete와 JPA Auditing
- 전역 예외 응답과 도메인별 에러 코드
- Swagger UI 문서

## 기술 스택

| 영역 | 기술 |
| --- | --- |
| Backend | Java 17, Spring Boot 3.3.1, Spring Web |
| Security | Spring Security, java-jwt |
| Persistence | Spring Data JPA, H2 |
| Docs / Build | springdoc-openapi, Gradle, Dockerfile |
| Test | JUnit 5, Spring Security Test |

## 도메인 관계

```text
Member
├── Post
├── Comment
├── Likes
└── MeetingMember ── Meeting

Post
├── Comment
└── Likes

Comment
└── Comment  # 대댓글
```

## 구조

```text
src/main/java/backend/backend/
├── configuration/  # Security, Swagger 설정
├── controller/     # Member, Post, Comment, Meeting API
├── domain/         # Entity, enum, DTO, 공통 응답
├── global/         # JWT, login filter, 예외, util
├── repository/     # JPA Repository
└── service/        # 비즈니스 로직
```

## 실행 방법

Gradle로 빌드, 테스트, 실행합니다.

```bash
./gradlew test
./gradlew bootRun
```

Docker로 실행할 수도 있습니다.

```bash
docker compose up --build
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다. Swagger UI는 다음 주소에서 확인합니다.

```text
http://localhost:8080/swagger-ui/index.html
```

## API 요약

| Method | Path | 설명 |
| --- | --- | --- |
| POST | `/login` | 로그인 |
| POST | `/api/signUp` | 회원가입 |
| GET | `/api/member` | 내 정보 조회 |
| GET | `/api/post` | 게시글 목록 |
| POST | `/api/post` | 게시글 작성 |
| GET | `/api/post/{id}` | 게시글 상세 |
| PUT | `/api/post/{id}` | 게시글 수정 |
| DELETE | `/api/post/{id}` | 게시글 삭제 |
| POST | `/api/post/{id}/like` | 좋아요 |
| DELETE | `/api/post/{id}/like` | 좋아요 취소 |
| POST | `/api/post/{id}/comment/{comment_id}` | 댓글/대댓글 작성 |
| GET | `/api/meeting` | 모임 목록 |
| POST | `/api/meeting` | 모임 생성 |
| GET | `/api/meeting/{meetingId}` | 모임 상세 |
| POST | `/api/meeting/{meetingId}` | 모임 참여 |

## 참고 사항

- 기본 DB는 H2 인메모리 DB이며 애플리케이션 재시작 시 데이터가 초기화됩니다.
- 프론트엔드 개발 서버(`http://localhost:3000`)와의 CORS 통신이 설정되어 있습니다.
