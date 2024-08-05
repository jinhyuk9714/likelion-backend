package backend.backend.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean isSuccess;
    private int code;
    private String message;
    private T result;

    // 성공 응답을 생성하는 static 메서드
    public static <T> ApiResponse<T> success(T result, String message) {
        return new ApiResponse<>(true, 1000, message, result);
    }

    // 실패 응답을 생성하는 static 메서드
    public static ApiResponse<Void> error(int code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}