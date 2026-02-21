package backend.backend.global.exception;

import backend.backend.domain.common.BusinessException;
import backend.backend.domain.common.ResponseCode;
import backend.backend.domain.dto.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Void>> handleBusinessException(BusinessException ex) {
        ResponseCode code = ex.getErrorCode();
        return new ResponseEntity<>(Response.fail(code), code.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response<Void>> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(
                Response.fail(ResponseCode.INTERNAL_ERROR),
                ResponseCode.INTERNAL_ERROR.getHttpStatus()
        );
    }
}
