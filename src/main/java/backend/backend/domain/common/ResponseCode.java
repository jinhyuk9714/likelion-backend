package backend.backend.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(200, "COM-000", "OK", HttpStatus.OK),

    // 인증 : AUTH
    AUTH_REQUIRED(401, "AUTH-001", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    AUTH_LOGIN_FAIL(401, "AUTH-002", "로그인에 실패했습니다.", HttpStatus.UNAUTHORIZED),

    // 댓글 : CMT
    CMT_AUTHENTICATION_FAIL(403, "CMT-101", "댓글 작성자 인증에 실패했습니다.", HttpStatus.FORBIDDEN),
    CMT_NOT_FOUND(404, "CMT-201", "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CMT_PARENT_NOT_FOUND(404, "CMT-202", "부모 댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CMT_POST_NOT_FOUND(404, "CMT-203", "게시물을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 게시글 : POS
    POS_AUTHENTICATION_FAIL(403, "POS-101", "게시글 작성자 인증에 실패했습니다.", HttpStatus.FORBIDDEN),
    POS_NOT_FOUND(404, "POS-201", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    POS_UPDATE_EXPIRED(400, "POS-301", "게시글 수정 기한이 만료되었습니다.", HttpStatus.BAD_REQUEST),

    // 좋아요 : LIK
    LIK_AUTHENTICATION_FAIL(403, "LIK-101", "좋아요 인증에 실패했습니다.", HttpStatus.FORBIDDEN),
    LIK_MEMBER_NOT_FOUND(404, "LIK-201", "해당 회원이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    LIK_POST_NOT_FOUND(404, "LIK-202", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    LIK_ALREADY_LIKED(409, "LIK-301", "이미 좋아요한 게시글입니다.", HttpStatus.CONFLICT),
    LIK_NOT_FOUND(404, "LIK-401", "좋아요를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 모임 : MTG
    MTG_AUTHENTICATION_FAIL(403, "MTG-101", "유저 인증에 실패했습니다.", HttpStatus.FORBIDDEN),
    MTG_NOT_FOUND(404, "MTG-201", "모임을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MTG_ALREADY_JOINED(409, "MTG-301", "이미 참여한 모임입니다.", HttpStatus.CONFLICT),
    MTG_CAPACITY_FULL(400, "MTG-302", "모임 정원이 초과되었습니다.", HttpStatus.BAD_REQUEST),

    // 회원 : MBR
    MBR_NOT_FOUND(404, "MBR-201", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MBR_ALREADY_EXISTS(409, "MBR-301", "이미 존재하는 아이디입니다.", HttpStatus.CONFLICT),

    // 서버 에러
    INTERNAL_ERROR(500, "SRV-001", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    final Integer status;
    final String code;
    final String message;
    final HttpStatus httpStatus;

}
