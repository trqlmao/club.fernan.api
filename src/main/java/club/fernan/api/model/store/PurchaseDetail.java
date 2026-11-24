package club.fernan.api.model.store;

import com.google.gson.annotations.SerializedName;
import java.util.Base64;
import java.util.List;

/**
 * Detailed view of a single purchase, including the full list of delivered accounts.
 * Distinct from {@link Purchase}: the {@code products} array uses {@code mc_uuid}
 * (versus {@code uuid}) and may carry {@link ProfileControls}.
 *
 * @author trq
 * @since 0.1.0
 */
public record PurchaseDetail(
        @SerializedName("purchase_id") String purchaseId,
        @SerializedName("product_id") int productId,
        @SerializedName("product_name") String productName,
        @SerializedName("product_description") String productDescription,
        @SerializedName("category") String category,
        @SerializedName("purchased_at") String purchasedAt,
        @SerializedName("amount") int amount,
        @SerializedName("total_paid") long totalPaid,
        @SerializedName("products") List<PurchaseDetailProduct> products,
        @SerializedName("refund_status") String refundStatus,
        @SerializedName("refund_id") String refundId,
        @SerializedName("refund_amount") Long refundAmount,
        @SerializedName("refund_denial_reason") String refundDenialReason,
        @SerializedName("can_refund") boolean canRefund) {

    /**
     * Per-account row inside a {@link PurchaseDetail}.
     *
     * @author trq
     * @since 0.1.0
     */
    public record PurchaseDetailProduct(
            @SerializedName("mc_uuid") String mcUuid,
            @SerializedName("data") String data,
            @SerializedName("username") String username,
            @SerializedName("access_token") String accessToken,
            @SerializedName("profile_controls") ProfileControls profileControls) {

        /** Decoded session/cookie data, or {@code null} if {@code data} is empty. */
        public String decodedData() {
            if (data == null || data.isBlank()) {
                return null;
            }
            try {
                return new String(Base64.getDecoder().decode(data));
            } catch (IllegalArgumentException e) {
                return data;
            }
        }
    }

    /**
     * Name and skin change state for a delivered account. Timestamps are {@code null} when
     * they do not apply.
     *
     * @author trq
     * @since 0.4.0
     */
    public record ProfileControls(
            @SerializedName("window_active") boolean windowActive,
            @SerializedName("can_change_name") boolean canChangeName,
            @SerializedName("can_change_skin") boolean canChangeSkin,
            @SerializedName("name_change_locked") boolean nameChangeLocked,
            @SerializedName("skin_cooldown_active") boolean skinCooldownActive,
            @SerializedName("window_expires_at") String windowExpiresAt,
            @SerializedName("skin_cooldown_ends_at") String skinCooldownEndsAt) {}
}
