package backend.backend.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(200, "COM-000", "OK", HttpStatus.OK),

    // 댓글 : CMT
    CMT_AUTHENTICATION_FAIL(400, " CMT-101", "유저 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CMT_NOT_FOUND(400,"CMT_201", "댓글을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    CMT_PARENT_NOT_FOUND(400, "CMT-202", "부모 댓글을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    CMT_POST_NOT_FOUND(400, "CMT-203", "게시물을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    // 좋아요 : LIK
    Lik_AUTHENTICATION_FAIL(400,"LIK-001", "유저 인증에 실패했습니다.", HttpStatus.BAD_REQUEST);

    final Integer status;
    final String code;
    final String message;
    final HttpStatus httpStatus;

}