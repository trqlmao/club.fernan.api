package club.fernan.api.service;

import club.fernan.api.http.JdkHttpTransport;
import club.fernan.api.model.refund.Refund;
import club.fernan.api.model.refund.RefundDetail;
import club.fernan.api.model.refund.RefundRequest;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;

/**
 * Refund endpoints: create, cancel, list, fetch detail.
 *
 * @author trq
 * @since 0.1.0
 */
@RequiredArgsConstructor
public final class RefundService {

    private final JdkHttpTransport http;

    /**
     * Create a refund request.
     *
     * @param purchaseId   UUID of the purchase being refunded.
     * @param productUuids 1-100 product UUIDs from that purchase.
     * @param reason       1-50 character reason.
     */
    public CompletableFuture<RefundRequest> create(String purchaseId, List<String> productUuids, String reason) {
        Map<String, Object> body = new HashMap<>();
        body.put("purchase_id", purchaseId);
        body.put("product_uuids", productUuids);
        body.put("reason", reason);
        return http.post("/store/refunds", body).thenApply(data -> http.gson().fromJson(data, RefundRequest.class));
    }

    /** Cancel a pending refund request. */
    public CompletableFuture<Void> cancel(String refundId) {
        return http.post("/store/refunds/cancel", Map.of("refund_id", refundId)).thenApply(data -> null);
    }

    /** Refund history with default pagination ({@code limit=20, offset=0}). */
    public CompletableFuture<List<Refund>> list() {
        return list(20, 0);
    }

    /** Refund history with explicit pagination. */
    public CompletableFuture<List<Refund>> list(int limit, int offset) {
        return http.get("/store/refunds?limit=" + limit + "&offset=" + offset).thenApply(data -> {
            JsonArray arr = data.getAsJsonArray("refunds");
            return http.gson().fromJson(arr, new TypeToken<List<Refund>>() {}.getType());
        });
    }

    /** Detail view of a single refund request. */
    public CompletableFuture<RefundDetail> get(String refundId) {
        return http.get("/store/refunds/" + refundId)
                .thenApply(data -> http.gson().fromJson(data.getAsJsonObject("refund"), RefundDetail.class));
    }
}
