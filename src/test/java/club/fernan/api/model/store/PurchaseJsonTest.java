package club.fernan.api.model.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class PurchaseJsonTest {

    private final Gson gson = new Gson();

    @Test
    void deserializes_purchase_with_products_and_referral() {
        String json =
                """
                {
                  "purchase_id": "abc-uuid",
                  "product": "Hypixel Alts",
                  "requested_amount": 2,
                  "delivered_amount": 2,
                  "invalid_count": 0,
                  "refunded_amount": 0,
                  "total_cost": 500,
                  "balance_before": 1000,
                  "balance_after": 500,
                  "products": [
                    {"data":"YWJj","username":"foo","uuid":"a0b1c2d3e4f5a0b1c2d3e4f5a0b1c2d3","access_token":"tok"}
                  ],
                  "referral_code": "creator1",
                  "referral_discount": 5
                }
                """;
        Purchase p = gson.fromJson(json, Purchase.class);
        assertEquals("abc-uuid", p.purchaseId());
        assertEquals(2, p.requestedAmount());
        assertEquals(2, p.deliveredAmount());
        assertTrue(p.fullyDelivered());
        assertTrue(p.hasReferralDiscount());
        assertNotNull(p.products());
        assertEquals(1, p.products().size());
        PurchasedAccount acct = p.products().get(0);
        assertEquals("foo", acct.username());
        assertEquals("abc", acct.decodedData());
        assertEquals("a0b1c2d3-e4f5-a0b1-c2d3-e4f5a0b1c2d3", acct.formattedUuid());
    }

    @Test
    void partial_delivery_detected() {
        String json =
                """
                {"purchase_id":"x","product":"X","requested_amount":5,"delivered_amount":3,
                 "invalid_count":1,"refunded_amount":1,"total_cost":300,"balance_before":300,
                 "balance_after":0,"products":[]}
                """;
        Purchase p = gson.fromJson(json, Purchase.class);
        assertFalse(p.fullyDelivered());
        assertFalse(p.hasReferralDiscount());
    }
}
