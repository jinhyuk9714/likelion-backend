package backend.backend.global.exception;

import backend.backend.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        int code = resolveErrorCode(message);  // 메시지에 따라 코드 설정
        return new ResponseEntity<>(ApiResponse.error(code, message), HttpStatus.BAD_REQUEST);
    }

    // 에러 메시지에 따라 코드 설정
    private int resolveErrorCode(String message) {
        switch (message) {
            case "해당 회원이 존재하지 않습니다.":
                return 2013;
            case "JWT 토큰을 입력해주세요.":
                return 2000;
            case "JWT 토큰 검증 실패":
                return 3000;
            case "userId를 입력해주세요.":
                return 2012;
            default:
                return 4000; // 기타 에러
        }
    }
}