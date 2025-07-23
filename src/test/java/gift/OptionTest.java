package gift;

import gift.domain.Option;
import gift.domain.Product;
import gift.dto.request.OptionRequest;
import gift.dto.request.ProductRequest;
import gift.exception.NotEnoughStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class OptionTest {

    @Test
    @DisplayName("수량을 차감할 수 있다")
    void 수량차감() {
        Option option = new Option("option", 10);

        option.substract(3);

        assertThat(option.getQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("재고보다 많이 차감하면 예외가 발생한다.")
    void 재고보다_많이_차감하면_예외가_발생한다() {
        Option option = new Option("기본 옵션", 2);

        assertThatThrownBy(() -> option.substract(5))
                .isInstanceOf(NotEnoughStockException.class);
    }
}
