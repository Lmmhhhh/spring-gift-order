package gift.dto.request;

import jakarta.validation.constraints.*;

public record OptionRequest(

        @NotBlank(message = "옵션 이름은 필수입니다.")
        @Size(max = 50, message = "옵션 이름은 공백 포함 최대 50자까지 입력할 수 있습니다.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s\\(\\)\\[\\]\\+\\-&/_]*$",
                message = "옵션 이름에는 특수문자 (),[],+,-,&,/,_ 만 포함될 수 있습니다.")
        String name,

        @Min(value = 1, message = "옵션 수량은 1 이상이어야 합니다.")
        @Max(value = 99_999_999, message = "옵션 수량은 1억 미만이어야 합니다.")
        int quantity
){}
