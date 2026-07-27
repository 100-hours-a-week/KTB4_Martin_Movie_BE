package com.homework4.workapi.handler;

import com.homework4.workapi.dto.common.CommonResponse;
import com.homework4.workapi.exception.BusinessException;
import com.homework4.workapi.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 직접 만든 NotFoundException 처리
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CommonResponse<Void>> handleNotFound(
            NotFoundException exception
    ) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(CommonResponse.of(
                        exception.getCode(),
                        null
                ));
    }

    // BusinessException과 이를 상속한 예외 처리
    // 예: AuthorizedException
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Void>> handleBusiness(
            BusinessException exception
    ) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(CommonResponse.of(
                        exception.getCode(),
                        null
                ));
    }

    // 서비스에서 던지는 ResponseStatusException 처리
    // 예: 작성자가 아닌 사용자의 게시글 수정 및 삭제
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CommonResponse<Void>> handleResponseStatus(
            ResponseStatusException exception
    ) {
        String message = exception.getReason();

        if (message == null) {
            message = "REQUEST_FAILED";
        }

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(CommonResponse.of(
                        message,
                        null
                ));
    }

    // @Valid 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    String defaultMessage =
                            fieldError.getDefaultMessage();

                    if (defaultMessage != null) {
                        return defaultMessage;
                    }

                    return fieldError.getField()
                            + " 값이 올바르지 않습니다.";
                })
                .findFirst()
                .orElse("입력값이 올바르지 않습니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.of(
                        message,
                        null
                ));
    }

    // 위에서 처리하지 못한 예상 밖의 서버 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleUnexpected(
            Exception exception
    ) {
        log.error("처리되지 않은 서버 오류가 발생했습니다.", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.of(
                        "INTERNAL_SERVER_ERROR",
                        null
                ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<CommonResponse<Void>>
    handleMaxUploadSize(
            MaxUploadSizeExceededException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(CommonResponse.of(
                        "파일 크기는 5MB 이하만 가능합니다.",
                        null
                ));
    }

    @ExceptionHandler({
            MissingRequestHeaderException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<CommonResponse<Void>> handleBadRequest(
            Exception exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(CommonResponse.of(
                        "요청 형식이 올바르지 않습니다.",
                        null
                ));
    }
}