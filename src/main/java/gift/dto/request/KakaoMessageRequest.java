package gift.dto.request;

import gift.domain.Order;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public record KakaoMessageRequest(String templateObject) {

    public MultiValueMap<String, String> toForm() {
        MultiValueMap<String, String> m = new LinkedMultiValueMap<>();
        m.add("template_object", templateObject);
        return m;
    }

    public static KakaoMessageRequest of(Order o) {
        String json = """
        {
          "object_type":"text",
          "text":"[주문 확인]\\n옵션: %s\\n수량: %d개\\n메시지: %s",
          "link":{"web_url":"https://gift.com/orders/%d"}
        }
        """.formatted(
                o.getOption().getName(),
                o.getQuantity(),
                o.getMessage(),
                o.getId()
        );
        return new KakaoMessageRequest(json);
    }
}