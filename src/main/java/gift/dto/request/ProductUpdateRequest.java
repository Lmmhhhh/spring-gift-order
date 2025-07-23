package gift.dto.request;

import gift.valid.NoKakao;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequest (
        @Size(max=15, message = "상품명은 공백 포함 최대 15자까지 입력할 수 있습니다.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s\\(\\)\\[\\]\\+\\-&/_]*$",
                message = "상품명에는 특수문자 (),[],+,-,&,/,_ 만 포함될 수 있습니다.")
        @NoKakao
        String name,

        @Positive(message = "상품 가격은 0보타 큰 값으로 입력해주세요")
        Integer price,

        String imageUrl
){}
