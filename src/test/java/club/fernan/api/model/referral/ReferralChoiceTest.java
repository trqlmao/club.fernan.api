package club.fernan.api.model.referral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReferralChoiceTest {

    @Test
    void of_present() {
        ReferralChoice c = ReferralChoice.of("creator1");
        assertTrue(c.isPresent());
        assertEquals("creator1", c.code());
    }

    @Test
    void none_absent() {
        ReferralChoice c = ReferralChoice.none();
        assertFalse(c.isPresent());
        assertNull(c.code());
    }

    @Test
    void blank_rejected() {
        assertThrows(IllegalArgumentException.class, () -> ReferralChoice.of(""));
        assertThrows(IllegalArgumentException.class, () -> ReferralChoice.of("   "));
        assertThrows(IllegalArgumentException.class, () -> ReferralChoice.of(null));
    }
}
