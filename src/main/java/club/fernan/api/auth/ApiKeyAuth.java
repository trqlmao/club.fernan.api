package club.fernan.api.auth;

import java.net.http.HttpRequest;
import java.util.Objects;

/**
 * Authenticates requests using the {@code X-API-Key} header.
 * This is the default and currently only supported auth mode for fernan.club.
 *
 * @author trq
 * @since 0.1.0
 */
public final class ApiKeyAuth implements AuthProvider {

    public static final String HEADER = "X-API-Key";

    private final String apiKey;

    public ApiKeyAuth(String apiKey) {
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey");
    }

    @Override
    public void apply(HttpRequest.Builder request) {
        request.header(HEADER, apiKey);
    }
}
