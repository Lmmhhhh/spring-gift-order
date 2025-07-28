package gift.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.response.KakaoErrorResponse;
import gift.exception.KakaoApiException;
import gift.exception.KakaoErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class KakaoResponseErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(KakaoResponseErrorHandler.class);
    private final ObjectMapper mapper;

    public KakaoResponseErrorHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String raw = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        KakaoErrorResponse err = mapper.readValue(raw, KakaoErrorResponse.class);
        KakaoErrorCode code = KakaoErrorCode.from(err);

        if (code == KakaoErrorCode.UNKNOWN) {
            HttpStatusCode status = response.getStatusCode();
            if (status.is4xxClientError()) {
                code = KakaoErrorCode.INVALID_REQUEST;
            } else if (status.is5xxServerError()) {
                code = KakaoErrorCode.INTERNAL_ERROR;
            }
        }

        log.error("Kakao API error {} - status={} body={}", code.name(), response.getStatusCode(), raw);
        throw new KakaoApiException(code.httpStatus(), code.userMessage());
    }
}