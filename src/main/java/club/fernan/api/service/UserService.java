package club.fernan.api.service;

import club.fernan.api.auth.ApiKeyAuth;
import club.fernan.api.http.JdkHttpTransport;
import club.fernan.api.model.user.RedemptionResult;
import club.fernan.api.model.user.User;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;

/**
 * User-scoped endpoints: profile, API-key management, balance redemption.
 *
 * <p>Note: these endpoints are not currently in the public fernan.txt docs but are
 * verified against the live API as of 2026-05-28.
 *
 * @author trq
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class UserService {

    private final JdkHttpTransport http;
    private final ApiKeyAuth auth;

    /** Current authenticated user. */
    public CompletableFuture<User> me() {
        return http.get("/user/me").thenApply(data -> http.gson().fromJson(data, User.class));
    }

    /**
     * Retrieve (or create) the user's API key.
     *
     * @since 0.2.0 (renamed from {@code getApiKey()}).
     */
    public CompletableFuture<String> apiKey() {
        return http.get("/user/key").thenApply(data -> data.get("api_key").getAsString());
    }

    /**
     * Rotate the API key, invalidating the previous one. The client's active key is
     * updated synchronously when the response arrives, and any listener registered via
     * {@code FernanClientBuilder.onApiKeyChange(...)} is fired with the new key.
     * Subsequent requests on this client use the rotated key without rebuild.
     */
    public CompletableFuture<String> regenerateApiKey() {
        return http.post("/user/key/regenerate", null).thenApply(data -> {
            String newKey = data.get("api_key").getAsString();
            auth.updateApiKey(newKey);
            return newKey;
        });
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
     * @since 0.3.0 (renamed from {@code getPreferredReferral()})
     */
    public CompletableFuture<String> preferredReferral() {
        throw new UnsupportedOperationException("Preferred-referral endpoint not yet documented by upstream API");
    }

    /**
     * Set the user's stored preferred referral code.
     *
     * <p>Endpoint shape not yet documented by the upstream API; implementation will land
     * once the upstream maintainers confirm the path. Calling today throws {@link UnsupportedOperationException}.
     *
     * @param code the referral code to store as the user's preference
     * @since 0.3.0 (renamed from {@code setPreferredReferral(String)})
     */
    public CompletableFuture<Void> preferredReferral(String code) {
        throw new UnsupportedOperationException("Preferred-referral endpoint not yet documented by upstream API");
    }
}
