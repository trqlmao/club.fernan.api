package club.fernan.api.model.refund;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class RefundDetailJsonTest {

    private final Gson gson = new Gson();

    @Test
    void deserializes_handled_refund() {
        String json =
                """
                {"refund_id":"r1","purchase_id":"p1","discord_id":"123","product_id":1,
                 "product_name":"X","requested_at":"2026-05-28T11:00:00Z","amount_to_refund":500,
                 "refund_reason":"bad","refunded_products":["u1","u2"],"status":"approved",
                 "status_code":1,"handled_by":"admin","handled_at":"2026-05-28T12:00:00Z",
                 "handled_reason":"valid","purchased_at":"2026-05-28T10:00:00Z"}
                """;
        RefundDetail d = gson.fromJson(json, RefundDetail.class);
        assertEquals("r1", d.refundId());
        assertEquals(2, d.refundedProducts().size());
        assertTrue(d.isHandled());
        assertFalse(d.isPending());
    }

    @Test
    void pending_detected_by_status_code_zero() {
        String json =
                """
                {"refund_id":"r1","purchase_id":"p1","discord_id":"123","product_id":1,
                 "product_name":"X","requested_at":"t","amount_to_refund":0,"refund_reason":"r",
                 "refunded_products":[],"status":"pending","status_code":0,
                 "handled_by":null,"handled_at":null,"handled_reason":null,"purchased_at":"t"}
                """;
        RefundDetail d = gson.fromJson(json, RefundDetail.class);
        assertTrue(d.isPending());
        assertFalse(d.isHandled());
    }
}
