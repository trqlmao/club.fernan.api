package club.fernan.api;

import club.fernan.api.http.JdkHttpTransport;
import club.fernan.api.service.HealthService;
import club.fernan.api.service.ReferralService;
import club.fernan.api.service.RefundService;
import club.fernan.api.service.StoreService;
import club.fernan.api.service.UserService;

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
 * User me = client.user().me().join();
 *
 * Purchase order = client.store()
 *         .purchase(1, 5, ReferralChoice.none())
 *         .join();
 *
 * client.shutdown();
 * }</pre>
 *
 * <p>All endpoints are asynchronous and return {@link java.util.concurrent.CompletableFuture}.
 * The default transport uses a virtual-thread executor; call {@link #shutdown()} when the
 * client is no longer needed.
 *
 * @author trq
 * @since 0.1.0
 */
public final class FernanClient {

    private final JdkHttpTransport http;
    private final UserService user;
    private final StoreService store;
    private final RefundService refunds;
    private final ReferralService referrals;
    private final HealthService health;

    FernanClient(JdkHttpTransport http) {
        this.http = http;
        this.user = new UserService(http);
        this.store = new StoreService(http);
        this.refunds = new RefundService(http);
        this.referrals = new ReferralService(http);
        this.health = new HealthService(http);
    }

    /** Start configuring a new client. */
    public static FernanClientBuilder builder() {
        return new FernanClientBuilder();
    }

    public UserService user() {
        return user;
    }

    public StoreService store() {
        return store;
    }

    public RefundService refunds() {
        return refunds;
    }

    public ReferralService referrals() {
        return referrals;
    }

    public HealthService health() {
        return health;
    }

    /** Release the underlying HTTP executor. Idempotent. */
    public void shutdown() {
        http.shutdown();
    }
}
