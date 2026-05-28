package club.fernan.api.service;

import club.fernan.api.http.JdkHttpTransport;
import club.fernan.api.model.referral.ReferralCode;
import club.fernan.api.model.referral.ReferralStats;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;

/**
 * Referral-code management for creators (MediaPlus+).
 *
 * <p>This service governs <em>owning</em> referral codes (creating, toggling,
 * deleting, viewing stats). To apply a referral code to a purchase, use
 * {@link StoreService#purchase} with a {@link club.fernan.api.model.referral.ReferralChoice}.
 *
 * @author trq
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class ReferralService {

    private final JdkHttpTransport http;

    /**
     * Create a new referral code.
     *
     * @param code            3-16 alphanumeric characters.
     * @param maxUses         Max uses, or {@code null} for unlimited (Partner+).
     * @param discountPercent Discount applied to redeemers (capped at 5% for non-admin).
     * @param expiresInDays   Days until expiration, or {@code null} for no expiration.
     */
    public CompletableFuture<ReferralCode> create(
            String code, Integer maxUses, Integer discountPercent, Integer expiresInDays) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        if (maxUses != null) body.put("max_uses", maxUses);
        if (discountPercent != null) body.put("discount_percent", discountPercent);
        if (expiresInDays != null) body.put("expires_in_days", expiresInDays);
        return http.post("/referrals/create", body)
                .thenApply(data -> http.gson().fromJson(data, ReferralCode.class));
    }

    /** List the user's owned referral codes. */
    public CompletableFuture<List<ReferralCode>> list() {
        return http.get("/referrals/codes").thenApply(data -> {
            JsonArray arr = data.getAsJsonArray("codes");
            return http.gson().fromJson(arr, new TypeToken<List<ReferralCode>>() {}.getType());
        });
    }

    /** Aggregate stats across the user's referral codes. */
    public CompletableFuture<ReferralStats> stats() {
        return http.get("/referrals/stats").thenApply(data -> http.gson().fromJson(data, ReferralStats.class));
    }

    /** Toggle a referral code's active status. */
    public CompletableFuture<Void> toggle(String code, boolean isActive) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("is_active", isActive);
        return http.post("/referrals/toggle", body).thenApply(data -> null);
    }

    /** Delete a referral code. */
    public CompletableFuture<Void> delete(String code) {
        return http.delete("/referrals/delete", Map.of("code", code)).thenApply(data -> null);
    }
}
