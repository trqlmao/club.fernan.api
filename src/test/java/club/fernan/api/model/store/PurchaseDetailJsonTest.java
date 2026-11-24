package club.fernan.api.model.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        assertNull(pdp.profileControls());
    }

    @Test
    void deserializes_profile_controls_and_refund_fields() {
        String json =
                """
                {"purchase_id":"p1","product_description":"Desc","refund_id":"r1","refund_amount":200,
                 "products":[{"mc_uuid":"u","data":"YWJj","username":"foo","access_token":"tok",
                   "profile_controls":{"window_active":true,"can_change_name":true,"can_change_skin":false,
                     "name_change_locked":false,"skin_cooldown_active":true,
                     "window_expires_at":"2026-05-19T12:00:00Z","skin_cooldown_ends_at":null}}]}
                """;
        PurchaseDetail d = gson.fromJson(json, PurchaseDetail.class);
        assertEquals("Desc", d.productDescription());
        assertEquals("r1", d.refundId());
        assertEquals(200L, d.refundAmount());
        var product = d.products().get(0);
        assertEquals("tok", product.accessToken());
        assertTrue(product.profileControls().windowActive());
        assertFalse(product.profileControls().canChangeSkin());
        assertEquals("2026-05-19T12:00:00Z", product.profileControls().windowExpiresAt());
        assertNull(product.profileControls().skinCooldownEndsAt());
    }
}
