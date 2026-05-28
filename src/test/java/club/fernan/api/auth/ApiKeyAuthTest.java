package club.fernan.api.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ApiKeyAuthTest {

    @Test
    void applies_header() {
        ApiKeyAuth auth = new ApiKeyAuth("initial-key");
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create("https://example.test/"));
        auth.apply(b);
        HttpRequest req = b.GET().build();
        assertEquals("initial-key", req.headers().firstValue("X-API-Key").orElseThrow());
    }

    @Test
    void update_swaps_active_key() {
        ApiKeyAuth auth = new ApiKeyAuth("old-key");
        auth.updateApiKey("new-key");
        assertEquals("new-key", auth.apiKey());

        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create("https://example.test/"));
        auth.apply(b);
        HttpRequest req = b.GET().build();
        assertEquals("new-key", req.headers().firstValue("X-API-Key").orElseThrow());
    }

    @Test
    void update_fires_registered_listeners_in_order() {
        ApiKeyAuth auth = new ApiKeyAuth("k1");
        List<String> received = new ArrayList<>();
        auth.addListener(k -> received.add("a:" + k));
        auth.addListener(k -> received.add("b:" + k));
        auth.updateApiKey("k2");
        assertEquals(List.of("a:k2", "b:k2"), received);
    }

    @Test
    void update_fires_listeners_on_every_rotation() {
        ApiKeyAuth auth = new ApiKeyAuth("k1");
        List<String> received = new ArrayList<>();
        auth.addListener(received::add);
        auth.updateApiKey("k2");
        auth.updateApiKey("k3");
        assertEquals(List.of("k2", "k3"), received);
    }

    @Test
    void null_key_rejected_at_construction() {
        assertThrows(NullPointerException.class, () -> new ApiKeyAuth(null));
    }

    @Test
    void null_key_rejected_on_update() {
        ApiKeyAuth auth = new ApiKeyAuth("k1");
        assertThrows(NullPointerException.class, () -> auth.updateApiKey(null));
    }

    @Test
    void null_listener_rejected() {
        ApiKeyAuth auth = new ApiKeyAuth("k1");
        assertThrows(NullPointerException.class, () -> auth.addListener(null));
    }
}
