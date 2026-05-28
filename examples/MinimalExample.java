import club.fernan.api.FernanClient;
import club.fernan.api.model.user.User;

/**
 * Smallest possible program: build a client, fetch the authenticated user,
 * print their balance, shut down.
 *
 * <p>This example uses {@code .join()} because it is a short-lived CLI
 * program and blocking the main thread is fine. <strong>Do not use
 * {@code .join()} inside a long-running application</strong> (UI thread,
 * game tick, Netty event loop, etc.) — chain with {@code .thenAccept} /
 * {@code .exceptionally} instead. See {@code AltManagerExample} for the
 * async pattern.
 *
 * <p>Run with the API key in an environment variable:
 * <pre>{@code
 *   FERNAN_KEY=your-key-here java MinimalExample
 * }</pre>
 */
public final class MinimalExample {

    public static void main(String[] args) {
        FernanClient client = FernanClient.builder()
                .apiKey(System.getenv("FERNAN_KEY"))
                .userAgent("minimal-example/1.0")
                .build();

        try {
            User me = client.user().me().join();
            System.out.println("Hello, " + me.username() + ". Balance: " + me.balance());
        } finally {
            client.shutdown();
        }
    }
}
