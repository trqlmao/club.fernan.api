package club.fernan.api.service;

import club.fernan.api.http.JdkHttpTransport;
import club.fernan.api.model.user.RedemptionResult;
import club.fernan.api.model.user.User;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * User-scoped endpoints: profile, API-key management, balance redemption.
 *
 * <p>Note: these endpoints are not currently in the public fernan.txt docs but are
 * verified against the live API as of 2026-05-28.
 *
 * @author trq
 * @since 0.1.0
 */
public final class UserService {

    private final JdkHttpTransport http;

    public UserService(JdkHttpTransport http) {
        this.http = http;
    }

    /** Current authenticated user. */
    public CompletableFuture<User> me() {
        return http.get("/user/me").thenApply(data -> http.gson().fromJson(data, User.class));
    }

    /** Retrieve (or create) the user's API key. */
    public CompletableFuture<String> getApiKey() {
        return http.get("/user/key").thenApply(data -> data.get("api_key").getAsString());
    }

    /** Rotate the API key, invalidating the previous one. */
    public CompletableFuture<String> regenerateApiKey() {
        return http.post("/user/key/regenerate", null)
                .thenApply(data -> data.get("api_key").getAsString());
    }

    /** Redeem a 24-character peso key. */
    public CompletableFuture<RedemptionResult> redeemKey(String key) {
        return http.post("/user/redeem", Map.of("key", key))
                .thenApply(data -> http.gson().fromJson(data, RedemptionResult.class));
    }

    /**
     * Get the user's stored preferred referral code, if any.
     *
     * <p>Endpoint shape not yet documented by the upstream API; implementation will land
     * once the upstream maintainers confirm the path. Calling today throws {@link UnsupportedOperationException}.
     *
     * @since 0.2.0
     */
    public CompletableFuture<String> getPreferredReferral() {
        throw new UnsupportedOperationException("Preferred-referral endpoint not yet documented by upstream API");
    }

    /**
     * Set the user's stored preferred referral code.
     *
     * <p>Endpoint shape not yet documented by the upstream API; implementation will land
     * once the upstream maintainers confirm the path. Calling today throws {@link UnsupportedOperationException}.
     *
     * @since 0.2.0
     */
    public CompletableFuture<Void> setPreferredReferral(String code) {
        throw new UnsupportedOperationException("Preferred-referral endpoint not yet documented by upstream API");
    }
}
