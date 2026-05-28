package club.fernan.api;

import club.fernan.api.auth.ApiKeyAuth;
import club.fernan.api.http.JdkHttpTransport;
import club.fernan.api.service.HealthService;
import club.fernan.api.service.ReferralService;
import club.fernan.api.service.RefundService;
import club.fernan.api.service.StoreService;
import club.fernan.api.service.UserService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Main entry point to the fernan.club API.
 *
 * <p>Build via {@link #builder()}, then access endpoints through the typed service
 * accessors:
 *
 * <pre>{@code
 * FernanClient client = FernanClient.builder()
 *         .apiKey(System.getenv("FERNAN_KEY"))
 *         .userAgent("my-app/1.0")
 *         .integration("my-app")
 *         .build();
 *
 * client.user().me().thenAccept(user -> render(user));
 *
 * client.store()
 *         .purchase(1, 5, ReferralChoice.none())
 *         .thenAccept(this::onPurchase)
 *         .exceptionally(t -> { onFailure(t); return null; });
 *
 * client.shutdown();
 * }</pre>
 *
 * <p>All endpoints are asynchronous and return {@link java.util.concurrent.CompletableFuture}.
 * Call {@link #shutdown()} when the client is no longer needed.
 *
 * @author trq
 * @since 0.1.0
 */
@Getter
@Accessors(fluent = true)
public final class FernanClient {

    @Getter(AccessLevel.NONE)
    private final JdkHttpTransport http;

    private final UserService user;
    private final StoreService store;
    private final RefundService refunds;
    private final ReferralService referrals;
    private final HealthService health;

    FernanClient(JdkHttpTransport http, ApiKeyAuth auth) {
        this.http = http;
        this.user = new UserService(http, auth);
        this.store = new StoreService(http);
        this.refunds = new RefundService(http);
        this.referrals = new ReferralService(http);
        this.health = new HealthService(http);
    }

    /** Start configuring a new client. */
    public static FernanClientBuilder builder() {
        return new FernanClientBuilder();
    }

    /** Release the underlying HTTP executor. Idempotent. */
    public void shutdown() {
        http.shutdown();
    }
}
