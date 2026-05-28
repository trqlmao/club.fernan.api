package club.fernan.api.model.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class PurchaseHistoryJsonTest {

    private final Gson gson = new Gson();

    @Test
    void deserializes_with_refund_pending() {
        String json =
                """
                {"purchase_id":"p1","product_id":1,"product_name":"X","category":"x",
                 "purchased_at":"2026-05-28T11:00:00Z","amount":3,"total_paid":300,
                 "refund_status":"pending","refund_id":"r1"}
                """;
        PurchaseHistory h = gson.fromJson(json, PurchaseHistory.class);
        assertEquals("p1", h.purchaseId());
        assertTrue(h.hasRefund());
        assertTrue(h.isRefundPending());
    }

    @Test
    void deserializes_without_refund() {
        String json =
                """
                {"purchase_id":"p1","product_id":1,"product_name":"X","category":"x",
                 "purchased_at":"2026-05-28T11:00:00Z","amount":3,"total_paid":300}
                """;
        PurchaseHistory h = gson.fromJson(json, PurchaseHistory.class);
        assertFalse(h.hasRefund());
        assertFalse(h.isRefundPending());
    }
}
