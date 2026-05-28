package club.fernan.api.model.store;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Result of a successful store purchase. Includes the delivered accounts,
 * cost breakdown, and referral metadata if a referral code was applied.
 *
 * @author trq
 * @since 0.1.0
 */
public record Purchase(
        @SerializedName("purchase_id") String purchaseId,
        @SerializedName("product") String product,
        @SerializedName("requested_amount") int requestedAmount,
        @SerializedName("delivered_amount") int deliveredAmount,
        @SerializedName("invalid_count") int invalidCount,
        @SerializedName("refunded_amount") int refundedAmount,
        @SerializedName("total_cost") long totalCost,
        @SerializedName("balance_before") long balanceBefore,
        @SerializedName("balance_after") long balanceAfter,
        @SerializedName("products") List<PurchasedAccount> products,
        @SerializedName("referral_code") String referralCode,
        @SerializedName("referral_discount") int referralDiscount) {

    /** True if every requested account was successfully delivered. */
    public boolean fullyDelivered() {
        return deliveredAmount == requestedAmount;
    }

    /** True if a referral code was recorded against the purchase. */
    public boolean hasReferralDiscount() {
        return referralCode != null && !referralCode.isBlank();
    }
}
