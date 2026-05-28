package club.fernan.api.model.referral;

import com.google.gson.annotations.SerializedName;

/**
 * A referral code owned by the authenticated user.
 *
 * @author trq
 * @since 0.1.0
 */
public record ReferralCode(
        @SerializedName("code") String code,
        @SerializedName("discord_id") String discordId,
        @SerializedName("created_at") String createdAt,
        @SerializedName("uses") int uses,
        @SerializedName("max_uses") Integer maxUses,
        @SerializedName("reward_percent") int rewardPercent,
        @SerializedName("reward_flat") int rewardFlat,
        @SerializedName("discount_percent") int discountPercent,
        @SerializedName("discount_flat") int discountFlat,
        @SerializedName("is_active") boolean isActive,
        @SerializedName("expires_at") String expiresAt) {

    /** True if {@code max_uses} is null (unlimited). */
    public boolean hasUnlimitedUses() {
        return maxUses == null;
    }

    /** True if unlimited or {@code uses < max_uses}. */
    public boolean hasRemainingUses() {
        return hasUnlimitedUses() || uses < maxUses;
    }

    /** Remaining uses, or {@code -1} if unlimited. */
    public int getRemainingUses() {
        if (hasUnlimitedUses()) {
            return -1;
        }
        return Math.max(0, maxUses - uses);
    }
}
