package club.fernan.api;

import club.fernan.api.auth.ApiKeyAuth;
import club.fernan.api.http.JdkHttpTransport;
import club.fernan.api.integration.IntegrationSignal;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Builder for {@link FernanClient}. The only required field is {@code apiKey};
 * everything else has sensible defaults.
 *
 * @author trq
 * @since 0.1.0
 */
public final class FernanClientBuilder {

    private static final String DEFAULT_BASE_URL = "https://api.fernan.club/api/v1";
    private static final String DEFAULT_USER_AGENT = "club.fernan.api/0.2.0 (java)";
    private static final int DEFAULT_CONNECT_TIMEOUT = 10_000;

    private String apiKey;
    private String baseUrl = DEFAULT_BASE_URL;
    private String userAgent = DEFAULT_USER_AGENT;
    private int connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT;
    private IntegrationSignal integration;
    private Consumer<String> onApiKeyChange;
    private ExecutorService executor;

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

    /**
     * Listener invoked whenever the API key rotates (typically via
     * {@code UserService.regenerateApiKey}). Useful for persisting the new key
     * to disk, a vault, an environment variable, etc. The listener runs
     * synchronously on the thread that completes the rotation call.
     *
     * @since 0.2.0
     */
    public FernanClientBuilder onApiKeyChange(Consumer<String> listener) {
        this.onApiKeyChange = listener;
        return this;
    }

    /**
     * Use the given executor for HttpClient async work. When set, callers own the
     * executor's lifecycle and {@link FernanClient#shutdown()} will NOT shut it down.
     * When unset, an internal cached daemon pool is created and managed by the client.
     *
     * @since 0.2.0
     */
    public FernanClientBuilder executor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public FernanClient build() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("API key is required");
        }
        ApiKeyAuth auth = new ApiKeyAuth(apiKey);
        if (onApiKeyChange != null) {
            auth.addListener(onApiKeyChange);
        }
        JdkHttpTransport http =
                new JdkHttpTransport(baseUrl, auth, userAgent, integration, connectTimeoutMillis, executor);
        return new FernanClient(http, auth);
    }
}
