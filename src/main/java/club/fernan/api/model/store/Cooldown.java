package club.fernan.api.model.store;

import com.google.gson.annotations.SerializedName;

/**
 * Cooldown status for a single product.
 *
 * @author trq
 * @since 0.1.0
 */
public record Cooldown(
        @SerializedName("product_id") int productId,
        @SerializedName("product_name") String productName,
        @SerializedName("category") String category,
        @SerializedName("remaining_allowed") int remainingAllowed,
        @SerializedName("cooldown_ends_at") String cooldownEndsAt,
        @SerializedName("on_cooldown") boolean onCooldown,
        @SerializedName("limit") int limit,
        @SerializedName("cooldown_minutes") int cooldownMinutes) {

    /** True when no cooldown is active and the user has remaining allowance. */
    public boolean canPurchase() {
        return !onCooldown && remainingAllowed > 0;
    }

    /** Clamp {@code desired} to what can actually be purchased right now. */
    public int maxPurchasable(int desired) {
        if (onCooldown) {
            return 0;
        }
        return Math.min(desired, remainingAllowed);
    }
}
