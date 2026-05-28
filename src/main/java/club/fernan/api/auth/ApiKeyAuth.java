package club.fernan.api.auth;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import lombok.NonNull;

/**
 * Authenticates requests using the {@code X-API-Key} header.
 *
 * <p>The stored key is mutable: callers (typically {@code UserService#regenerateApiKey})
 * can swap it via {@link #updateApiKey(String)} and any registered listeners are notified
 * synchronously. Useful for persisting rotated keys to disk, a vault, etc.
 *
 * <p>This is the default auth mode for fernan.club; alternative {@link AuthProvider}
 * implementations can be plugged in once the upstream API supports them.
 *
 * @author trq
 * @since 0.1.0
 */
public final class ApiKeyAuth implements AuthProvider {

    public static final String HEADER = "X-API-Key";

    private volatile String apiKey;
    private final List<Consumer<String>> listeners = new CopyOnWriteArrayList<>();

    public ApiKeyAuth(@NonNull String apiKey) {
        this.apiKey = apiKey;
    }

    /** Currently active API key. */
    public String apiKey() {
        return apiKey;
    }

    /**
     * Replace the stored key and notify all registered listeners synchronously.
     * Listener exceptions are not swallowed; they propagate to the caller.
     *
     * @param newKey The new API key. Must be non-null.
     */
    public void updateApiKey(@NonNull String newKey) {
        this.apiKey = newKey;
        for (Consumer<String> listener : listeners) {
            listener.accept(newKey);
        }
    }

    /**
     * Register a listener invoked whenever {@link #updateApiKey(String)} is called.
     * Listeners are invoked in registration order.
     *
     * @param listener The callback to register. Must be non-null.
     */
    public void addListener(Consumer<String> listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    @Override
    public void apply(HttpRequest.Builder request) {
        request.header(HEADER, apiKey);
    }
}
