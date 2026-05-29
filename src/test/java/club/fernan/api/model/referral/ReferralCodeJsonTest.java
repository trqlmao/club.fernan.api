package club.fernan.api.model.referral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class ReferralCodeJsonTest {

    private final Gson gson = new Gson();

    @Test
    void unlimited_uses_when_max_null() {
        String json =
                """
                {"code":"creator1","discord_id":"123","created_at":"t","uses":42,"max_uses":null,
                 "reward_percent":5,"reward_flat":0,"discount_percent":5,"discount_flat":0,
                 "is_active":true,"expires_at":null}
                """;
        ReferralCode c = gson.fromJson(json, ReferralCode.class);
        assertTrue(c.hasUnlimitedUses());
        assertTrue(c.hasRemainingUses());
        assertEquals(-1, c.remainingUses());
    }

    @Test
    void capped_uses_computed_correctly() {
        String json =
                """
                {"code":"creator2","discord_id":"123","created_at":"t","uses":7,"max_uses":10,
                 "reward_percent":5,"reward_flat":0,"discount_percent":5,"discount_flat":0,
                 "is_active":true,"expires_at":"t"}
                """;
        ReferralCode c = gson.fromJson(json, ReferralCode.class);
        assertFalse(c.hasUnlimitedUses());
        assertTrue(c.hasRemainingUses());
        assertEquals(3, c.remainingUses());
    }
}
