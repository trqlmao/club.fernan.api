package club.fernan.api.model.refund;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class RefundJsonTest {

    private final Gson gson = new Gson();

    @Test
    void deserializes_pending_refund() {
        String json =
                """
                {"refund_id":"r1","purchase_id":"p1","discord_id":"123","product_name":"X",
                 "requested_at":"2026-05-28T11:00:00Z","amount_to_refund":500,"item_count":2,
                 "status":"pending","handled_at":null}
                """;
        Refund r = gson.fromJson(json, Refund.class);
        assertEquals("r1", r.refundId());
        assertTrue(r.isPending());
        assertFalse(r.isApproved());
    }

    @Test
    void status_helpers_case_insensitive() {
        Refund r = new Refund("r", "p", "d", "n", "t", 0L, 0, "Approved", "t2");
        assertTrue(r.isApproved());
        assertFalse(r.isPending());
        assertFalse(r.isDenied());
        assertFalse(r.isCancelled());
    }
}
