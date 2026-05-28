package club.fernan.api.model.health;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class HealthStatusJsonTest {

    @Test
    void all_healthy() {
        String json =
                """
                {"status":"healthy","services":{"db":"healthy","cache":"healthy"}}
                """;
        HealthStatus h = new Gson().fromJson(json, HealthStatus.class);
        assertTrue(h.isHealthy());
        assertFalse(h.isDegraded());
        assertTrue(h.isServiceHealthy("db"));
        assertFalse(h.isServiceHealthy("missing"));
    }

    @Test
    void degraded_state() {
        String json =
                """
                {"status":"degraded","services":{"db":"healthy","cache":"down"}}
                """;
        HealthStatus h = new Gson().fromJson(json, HealthStatus.class);
        assertTrue(h.isDegraded());
        assertFalse(h.isHealthy());
        assertFalse(h.isServiceHealthy("cache"));
    }
}
