package club.fernan.api.model.refund;

import com.google.gson.annotations.SerializedName;

/**
 * Result of creating a refund request.
 *
 * @author trq
 * @since 0.1.0
 */
public record RefundRequest(
        @SerializedName("message") String message,
        @SerializedName("refund_id") String refundId,
        @SerializedName("purchase_id") String purchaseId,
        @SerializedName("amount_to_refund") long amountToRefund,
        @SerializedName("item_count") int itemCount,
        @SerializedName("status") String status,
        @SerializedName("requested_at") String requestedAt) {}
