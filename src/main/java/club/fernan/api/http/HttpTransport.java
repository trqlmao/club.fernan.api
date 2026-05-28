package club.fernan.api.http;

import com.google.gson.JsonObject;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous HTTP transport for the fernan.club API.
 * Implementations are responsible for request construction (path resolution,
 * authentication, headers) and for translating responses into
 * {@code CompletableFuture<JsonObject>} representing the response envelope's
 * {@code data} field, or failing the future with a
 * {@link club.fernan.api.exception.FernanException}.
 *
 * <p>The default implementation is {@link JdkHttpTransport}, built on
 * {@code java.net.http.HttpClient}. Custom transports (OkHttp, Vert.x, etc.)
 * can be plugged in via the builder.
 *
 * @author trq
 * @since 0.1.0
 */
public interface HttpTransport {

    /** Performs a GET request against {@code path}. */
    CompletableFuture<JsonObject> get(String path);

    /** Performs a POST request against {@code path} with a JSON body (may be {@code null}). */
    CompletableFuture<JsonObject> post(String path, Object body);

    /** Performs a DELETE request against {@code path} with a JSON body (may be {@code null}). */
    CompletableFuture<JsonObject> delete(String path, Object body);

    /** Shuts down underlying resources (executors, connection pools). */
    void shutdown();
}
