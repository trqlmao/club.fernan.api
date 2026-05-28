package club.fernan.api.model.store;

import com.google.gson.annotations.SerializedName;

/**
 * Summary row in the user's purchase history.
 *
 * @author trq
 * @since 0.1.0
 */
public record PurchaseHistory(
        @SerializedName("purchase_id") String purchaseId,
        @SerializedName("product_id") int productId,
        @SerializedName("product_name") String productName,
        @SerializedName("category") String category,
        @SerializedName("purchased_at") String purchasedAt,
        @SerializedName("amount") int amount,
        @SerializedName("total_paid") long totalPaid,
        @SerializedName("refund_status") String refundStatus,
        @SerializedName("refund_id") String refundId) {

    /** True if a refund has been requested for this purchase. */
    public boolean hasRefund() {
        return refundId != null && !refundId.isBlank();
    }

    /** True if {@code refund_status} is {@code "pending"}. */
    public boolean isRefundPending() {
        return "pending".equalsIgnoreCase(refundStatus);
    }
}
