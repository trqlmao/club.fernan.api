package club.fernan.api.model.refund;

import com.google.gson.annotations.SerializedName;

/**
 * Summary row in the user's refund history.
 *
 * @author trq
 * @since 0.1.0
 */
public record Refund(
        @SerializedName("refund_id") String refundId,
        @SerializedName("purchase_id") String purchaseId,
        @SerializedName("discord_id") String discordId,
        @SerializedName("product_name") String productName,
        @SerializedName("requested_at") String requestedAt,
        @SerializedName("amount_to_refund") long amountToRefund,
        @SerializedName("item_count") int itemCount,
        @SerializedName("status") String status,
        @SerializedName("handled_at") String handledAt) {

    public boolean isPending() {
        return "pending".equalsIgnoreCase(status);
    }

    public boolean isApproved() {
        return "approved".equalsIgnoreCase(status);
    }

    public boolean isDenied() {
        return "denied".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "cancelled".equalsIgnoreCase(status);
    }
}
