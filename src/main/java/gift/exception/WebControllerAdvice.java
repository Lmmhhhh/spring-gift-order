package gift.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class WebControllerAdvice {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationExceptions(MethodArgumentNotValidException e){
        Map<String, List<String>> errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()){
            errors.computeIfAbsent(error.getField(), key -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(Map.of("message", e.getReason()));
    }

    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<Map<String,String>> handleDuplicateMember (DuplicateMemberException e){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message",e.getMessage()));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleMemberNotFound (MemberNotFoundException e){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message",e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String,String>> handleUnauth(UnauthorizedException e){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message",e.getMessage()));
    }

    @ExceptionHandler(WishNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleWishNotFound (WishNotFoundException e){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message",e.getMessage()));
    }

    @ExceptionHandler(DuplicateWishException.class)
    public ResponseEntity<Map<String,String>> handleDuplicateWish(DuplicateWishException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message",e.getMessage()));
    }

    @ExceptionHandler(ProductOptionEmptyException.class)
    public ResponseEntity<Map<String,String>> handleEmptyOption(ProductOptionEmptyException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message",e.getMessage()));
    }

    @ExceptionHandler(DuplicateOptionNameException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateOption(DuplicateOptionNameException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<Map<String, String>> handleNotEnoughStock(NotEnoughStockException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(InvalidOptionNameException.class)
    public ResponseEntity<Map<String, String>> handleInvalidOptionName(InvalidOptionNameException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(KakaoApiException.class)
    public ResponseEntity<Map<String, String>> handleKakaoApi(KakaoApiException e) {
        return ResponseEntity.status(e.getStatus())
                .body(Map.of("message", e.getMessage()));
    }

}