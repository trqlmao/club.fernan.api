package club.fernan.api.integration;

import java.util.Objects;

/**
 * Optional partner-identification signal sent with every request via the
 * {@code X-Integration} header. Lets fernan.club attribute traffic to a specific
 * integration (mod, client, third-party app) without granting referral revenue.
 *
 * <p>Set on the client via {@code FernanClientBuilder.integration("my-app")}.
 * The exact server-side handling is subject to change; the wrapper currently
 * sends it as a header so the call site stays stable when the upstream API
 * formalizes the field.
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
