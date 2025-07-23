package gift.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoKakaoValidator implements ConstraintValidator<NoKakao,String> {
    @Override
    public boolean isValid(String val, ConstraintValidatorContext context){
        if (val == null) return true;
        return !val.contains("카카오");
    }
}
