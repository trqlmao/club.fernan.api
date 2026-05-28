package club.fernan.api.model.referral;

import com.google.gson.annotations.SerializedName;

/**
 * Result of validating a referral code prior to purchase.
 *
 * @author trq
 * @since 0.1.0
 */
public record ReferralValidation(boolean valid, String code, @SerializedName("discount_percent") int discountPercent) {}
