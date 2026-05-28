package club.fernan.api.model.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class CooldownJsonTest {

    private final Gson gson = new Gson();

    @Test
    void deserializes_cooldown_record() {
        String json =
                """
                {
                  "product_id": 1,
                  "product_name": "Hypixel Alts",
                  "category": "hypixel",
                  "remaining_allowed": 3,
                  "cooldown_ends_at": "2026-05-28T12:00:00Z",
                  "on_cooldown": false,
                  "limit": 10,
                  "cooldown_minutes": 60
                }
                """;
        Cooldown c = gson.fromJson(json, Cooldown.class);
        assertEquals(1, c.productId());
        assertEquals(3, c.remainingAllowed());
        assertFalse(c.onCooldown());
        assertTrue(c.canPurchase());
        assertEquals(3, c.getMaxPurchasable(10));
        assertEquals(2, c.getMaxPurchasable(2));
    }

    @Test
    void on_cooldown_blocks_purchase() {
        String json =
                """
                {"product_id":1,"product_name":"x","category":"x","remaining_allowed":5,
                 "cooldown_ends_at":"2026-05-28T12:00:00Z","on_cooldown":true,"limit":10,
                 "cooldown_minutes":60}
                """;
        Cooldown c = gson.fromJson(json, Cooldown.class);
        assertFalse(c.canPurchase());
        assertEquals(0, c.getMaxPurchasable(5));
    }
}
