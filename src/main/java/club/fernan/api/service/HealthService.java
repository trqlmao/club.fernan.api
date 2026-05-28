package club.fernan.api.service;

import club.fernan.api.http.JdkHttpTransport;
import club.fernan.api.model.health.HealthStatus;
import java.util.concurrent.CompletableFuture;

/**
 * Health-check endpoints.
 *
 * @author trq
 * @since 0.1.0
 */
public final class HealthService {

    private final JdkHttpTransport http;

    public HealthService(JdkHttpTransport http) {
        this.http = http;
    }

    /** Full health check, including downstream dependencies. */
    public CompletableFuture<HealthStatus> get() {
        return http.get("/health").thenApply(data -> http.gson().fromJson(data, HealthStatus.class));
    }

    /** Lightweight liveness probe. Returns {@code true} when {@code status == "healthy"}. */
    public CompletableFuture<Boolean> simple() {
        return http.get("/health/simple")
                .thenApply(data -> "healthy".equals(data.get("status").getAsString()));
    }
}
