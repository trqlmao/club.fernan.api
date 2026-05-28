package club.fernan.api.model.referral;

import com.google.gson.annotations.SerializedName;

/**
 * Aggregate statistics across the user's referral codes.
 *
 * @author trq
 * @since 0.1.0
 */
public record ReferralStats(
        @SerializedName("total_codes") int totalCodes,
        @SerializedName("total_uses") int totalUses,
        @SerializedName("total_earnings") long totalEarnings) {}
