package club.fernan.api.integration;

import java.util.Objects;

/**
 * Optional application name sent with every request in the {@code X-Integration} header.
 * It carries no referral.
 *
 * <p>Set on the client via {@code FernanClientBuilder.integration("my-app")}.
 * The public API reference does not document this header, so the server may ignore it.
 *
 * @author trq
 * @since 0.1.0
 */
public record IntegrationSignal(String id) {

    public static final String HEADER = "X-Integration";

    public IntegrationSignal {
        Objects.requireNonNull(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("integration id must not be blank");
        }
    }

    public static IntegrationSignal of(String id) {
        return new IntegrationSignal(id);
    }
}
