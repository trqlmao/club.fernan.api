package club.fernan.api.model.refund;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class RefundRequestJsonTest {

    @Test
    void deserializes_refund_create_response() {
        String json =
                """
                {"message":"queued","refund_id":"r1","purchase_id":"p1","amount_to_refund":500,
                 "item_count":2,"status":"pending","requested_at":"2026-05-28T11:00:00Z"}
                """;
        RefundRequest r = new Gson().fromJson(json, RefundRequest.class);
        assertEquals("r1", r.refundId());
        assertEquals("pending", r.status());
        assertEquals(2, r.itemCount());
    }
}
