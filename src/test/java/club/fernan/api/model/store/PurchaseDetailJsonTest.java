package club.fernan.api.model.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class PurchaseDetailJsonTest {

    private final Gson gson = new Gson();

    @Test
    void deserializes_purchase_detail_with_products() {
        String json =
                """
                {
                  "purchase_id": "p1",
                  "product_id": 1,
                  "product_name": "Hypixel Alts",
                  "category": "hypixel",
                  "purchased_at": "2026-05-28T11:00:00Z",
                  "amount": 1,
                  "total_paid": 250,
                  "products": [
                    {"mc_uuid":"a0b1c2d3e4f5a0b1c2d3e4f5a0b1c2d3","data":"YWJj","username":"foo"}
                  ],
                  "refund_status": null,
                  "refund_denial_reason": null,
                  "can_refund": true
                }
                """;
        PurchaseDetail d = gson.fromJson(json, PurchaseDetail.class);
        assertEquals("p1", d.purchaseId());
        assertTrue(d.canRefund());
        assertEquals(1, d.products().size());
        PurchaseDetail.PurchaseDetailProduct pdp = d.products().get(0);
        assertEquals("foo", pdp.username());
        assertEquals("abc", pdp.decodedData());
    }
}
