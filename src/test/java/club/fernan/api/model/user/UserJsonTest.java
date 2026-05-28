package club.fernan.api.model.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class UserJsonTest {

    private final Gson gson = new Gson();

    @Test
    void deserializes_user() {
        String json =
                """
                {"discord_id":"123","username":"foo","avatar_url":"https://cdn.test/a.png",
                 "balance":1000,"role_id":3}
                """;
        User u = gson.fromJson(json, User.class);
        assertEquals("123", u.discordId());
        assertEquals("foo", u.username());
        assertEquals(1000L, u.balance());
        assertTrue(u.hasMediaPlus());
        assertFalse(u.hasPartner());
        assertFalse(u.isAdmin());
    }

    @Test
    void role_helpers() {
        User free = new User("d", "u", "a", 0L, 0);
        User media = new User("d", "u", "a", 0L, 3);
        User partner = new User("d", "u", "a", 0L, 5);
        User admin = new User("d", "u", "a", 0L, 7);
        assertFalse(free.hasMediaPlus());
        assertTrue(media.hasMediaPlus());
        assertTrue(partner.hasPartner());
        assertTrue(admin.isAdmin());
    }
}
