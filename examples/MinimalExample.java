import club.fernan.api.FernanClient;
import club.fernan.api.model.user.User;

/**
 * Smallest possible program: build a client, fetch the authenticated user,
 * print their balance, shut down.
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
