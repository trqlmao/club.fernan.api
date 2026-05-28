package club.fernan.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import club.fernan.api.integration.IntegrationSignal;
import org.junit.jupiter.api.Test;

class FernanClientBuilderTest {

    @Test
    void missing_api_key_throws() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class, () -> FernanClient.builder().build());
        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("API key"));
    }

    @Test
    void blank_api_key_throws() {
        assertThrows(
                IllegalStateException.class,
                () -> FernanClient.builder().apiKey("   ").build());
    }

    @Test
    void defaults_build_successfully() {
        FernanClient client =
                assertDoesNotThrow(() -> FernanClient.builder().apiKey("test").build());
        client.shutdown();
    }

    @Test
    void all_builder_options_compose() {
        FernanClient client = FernanClient.builder()
                .apiKey("test")
                .baseUrl("https://example.test/api/v1")
                .userAgent("custom-ua/9.9")
                .connectTimeoutMillis(5_000)
                .integration("my-app")
                .build();
        client.shutdown();
    }

    @Test
    void integration_signal_passthrough() {
        IntegrationSignal sig = IntegrationSignal.of("my-app");
        FernanClient client =
                FernanClient.builder().apiKey("test").integration(sig).build();
        client.shutdown();
    }

    @Test
    void blank_integration_id_rejected() {
        assertThrows(IllegalArgumentException.class, () -> IntegrationSignal.of(""));
        assertThrows(IllegalArgumentException.class, () -> IntegrationSignal.of("   "));
    }
}
