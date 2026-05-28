package club.fernan.api;

import club.fernan.api.auth.ApiKeyAuth;
import club.fernan.api.http.JdkHttpTransport;
import club.fernan.api.integration.IntegrationSignal;

/**
 * Builder for {@link FernanClient}. The only required field is {@code apiKey};
 * everything else has sensible defaults.
 *
 * @author trq
 * @since 0.1.0
 */
public final class FernanClientBuilder {

    private static final String DEFAULT_BASE_URL = "https://api.fernan.club/api/v1";
    private static final String DEFAULT_USER_AGENT = "club.fernan.api/0.1.0 (java)";
    private static final int DEFAULT_CONNECT_TIMEOUT = 10_000;

    private String apiKey;
    private String baseUrl = DEFAULT_BASE_URL;
    private String userAgent = DEFAULT_USER_AGENT;
    private int connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT;
    private IntegrationSignal integration;

    FernanClientBuilder() {}

    /** API key from the fernan.club dashboard. Required. */
    public FernanClientBuilder apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /** Override the API base URL. Trailing slash is stripped. */
    public FernanClientBuilder baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /** Override the {@code User-Agent} header sent with every request. */
    public FernanClientBuilder userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /** Connect timeout in milliseconds. Defaults to 10 000. */
    public FernanClientBuilder connectTimeoutMillis(int millis) {
        this.connectTimeoutMillis = millis;
        return this;
    }

    /**
     * Identify the calling integration for partner-attribution tracking.
     * Sent as the {@code X-Integration} header on every request.
     */
    public FernanClientBuilder integration(String id) {
        this.integration = IntegrationSignal.of(id);
        return this;
    }

    /** Identify the calling integration with a pre-built signal. */
    public FernanClientBuilder integration(IntegrationSignal signal) {
        this.integration = signal;
        return this;
    }

    public FernanClient build() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("API key is required");
        }
        JdkHttpTransport http =
                new JdkHttpTransport(baseUrl, new ApiKeyAuth(apiKey), userAgent, integration, connectTimeoutMillis);
        return new FernanClient(http);
    }
}
