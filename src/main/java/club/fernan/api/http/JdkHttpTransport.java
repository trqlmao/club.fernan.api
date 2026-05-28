package club.fernan.api.http;

import club.fernan.api.auth.AuthProvider;
import club.fernan.api.integration.IntegrationSignal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default {@link HttpTransport} backed by {@link java.net.http.HttpClient} with
 * a virtual-thread executor.
 *
 * @author trq
 * @since 0.1.0
 */
public final class JdkHttpTransport implements HttpTransport {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ExecutorService executor;
    private final Gson gson;
    private final AuthProvider auth;
    private final String userAgent;
    private final IntegrationSignal integration;

    public JdkHttpTransport(
            String baseUrl,
            AuthProvider auth,
            String userAgent,
            IntegrationSignal integration,
            int connectTimeoutMillis) {
        this.baseUrl = stripTrailingSlash(baseUrl);
        this.auth = auth;
        this.userAgent = userAgent;
        this.integration = integration;
        this.executor = Executors.newCachedThreadPool(daemonFactory());
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMillis))
                .executor(executor)
                .build();
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();
    }

    /** Exposes the configured Gson instance for service-side deserialization. */
    public Gson gson() {
        return gson;
    }

    @Override
    public CompletableFuture<JsonObject> get(String path) {
        HttpRequest request = newRequest(path).GET().build();
        return execute(request);
    }

    @Override
    public CompletableFuture<JsonObject> post(String path, Object body) {
        String json = body != null ? gson.toJson(body) : "{}";
        HttpRequest request = newRequest(path)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        return execute(request);
    }

    @Override
    public CompletableFuture<JsonObject> delete(String path, Object body) {
        String json = body != null ? gson.toJson(body) : "{}";
        HttpRequest request = newRequest(path)
                .method("DELETE", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        return execute(request);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    private HttpRequest.Builder newRequest(String path) {
        String url = baseUrl + (path.startsWith("/") ? path : "/" + path);
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", userAgent)
                .header("Accept", "application/json");
        auth.apply(b);
        if (integration != null) {
            b.header(IntegrationSignal.HEADER, integration.id());
        }
        return b;
    }

    private CompletableFuture<JsonObject> execute(HttpRequest request) {
        return httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(ResponseHandler::handle);
    }

    private static String stripTrailingSlash(String s) {
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }

    private static ThreadFactory daemonFactory() {
        AtomicLong id = new AtomicLong();
        return r -> {
            Thread t = new Thread(r, "fernan-api-" + id.incrementAndGet());
            t.setDaemon(true);
            return t;
        };
    }
}
