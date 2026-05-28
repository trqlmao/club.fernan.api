package club.fernan.api.model.refund;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Detailed view of a single refund request, including handling metadata.
 *
 * @author trq
 * @since 0.1.0
 */
public record RefundDetail(
        @SerializedName("refund_id") String refundId,
        @SerializedName("purchase_id") String purchaseId,
        @SerializedName("discord_id") String discordId,
        @SerializedName("product_id") int productId,
        @SerializedName("product_name") String productName,
        @SerializedName("requested_at") String requestedAt,
        @SerializedName("amount_to_refund") long amountToRefund,
        @SerializedName("refund_reason") String refundReason,
        @SerializedName("refunded_products") List<String> refundedProducts,
        String status,
        @SerializedName("status_code") int statusCode,
        @SerializedName("handled_by") String handledBy,
        @SerializedName("handled_at") String handledAt,
        @SerializedName("handled_reason") String handledReason,
        @SerializedName("purchased_at") String purchasedAt) {

    public boolean isHandled() {
        return handledAt != null;
    }

    /** True if {@code status_code == 0} (pending). */
    public boolean isPending() {
        return statusCode == 0;
    }
}
