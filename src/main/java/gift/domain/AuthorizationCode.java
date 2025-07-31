package gift.domain;

import java.util.Objects;

public final class AuthorizationCode {

    private final String value;

    public static AuthorizationCode of(String raw) {
        Objects.requireNonNull(raw, "인가코드는 null일 수 없습니다.");
        return new AuthorizationCode(raw);
    }

    public AuthorizationCode(String value) {
        this.value = value;
    }

    public String raw() {
        return value;
    }

    public String mask() {
        if (value.length() <= 4) {
            return "****";
        }
        return value.substring(0, 4) + "****";
    }
}
