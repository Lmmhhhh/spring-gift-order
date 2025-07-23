package gift.valid;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoKakaoValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoKakao {
    String message() default "상품명에 '카카오'를 포함할 수 없습니다. 담당자에게 문의하세요.";
    Class[] groups() default {};
    Class[] payload() default {};
}
