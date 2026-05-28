import club.fernan.api.FernanClient;
import club.fernan.api.exception.ErrorType;
import club.fernan.api.exception.FernanException;
import club.fernan.api.model.referral.ReferralChoice;
import club.fernan.api.model.store.Product;
import club.fernan.api.model.store.PurchasedAccount;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Canonical reference for wiring this library into a client's alt-storage layer.
 *
 * <p>Defines a tiny {@link AltStore} interface representing whatever your host
 * application uses to remember credentials. Walks through the typical flow
 * with proper async chaining (no {@code .join()} in business logic):
 *
 * <ol>
 *   <li>List available stock for the user.</li>
 *   <li>Ask the user which referral code (if any) to apply, validating the
 *       chosen code asynchronously.</li>
 *   <li>Issue the purchase.</li>
 *   <li>For each delivered account, decode the session blob and register it
 *       with your alt store.</li>
 * </ol>
 *
 * <p>The {@link CountDownLatch} at the bottom is purely so this {@code main}
 * doesn't return before the async chain completes. In a real host application
 * (mod, launcher, etc.), the host's own event loop keeps the process alive
 * and you do not need a latch.
 */
public final class AltManagerExample {

    /**
     * Plug-point for the host application's credential store. Anything that
     * accepts {@code (username, uuid, sessionBlob)} qualifies.
     */
    public interface AltStore {
        void register(String username, String uuid, String sessionBlob);
    }

    public static void main(String[] args) throws InterruptedException {
        FernanClient client = FernanClient.builder()
                .apiKey(System.getenv("FERNAN_KEY"))
                .userAgent("my-mod/1.0")
                .integration("my-mod")
                .build();

        AltStore altStore = (u, id, blob) -> System.out.println("Stored " + u + " (" + id + ")");
        CountDownLatch done = new CountDownLatch(1);

        // 1. Browse stock, 2. resolve referral choice, 3. purchase, 4. register accounts.
        client.store()
                .getStock()
                .thenCompose(stock -> {
                    Product chosen = stock.stream()
                            .filter(Product::inStock)
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("No stock available"));
                    System.out.println("Buying: " + chosen.productName() + " @ " + chosen.price());

                    return resolveReferralChoice(client).thenCompose(referral -> {
                        int quantity = 1;
                        return client.store().purchase(chosen.productId(), quantity, referral);
                    });
                })
                .thenAccept(result -> {
                    for (PurchasedAccount account : result.products()) {
                        altStore.register(account.username(), account.formattedUuid(), account.decodedData());
                    }
                    System.out.println("Delivered " + result.deliveredAmount() + "/" + result.requestedAmount());
                })
                .exceptionally(t -> {
                    handleFailure(t);
                    return null;
                })
                .whenComplete((__, ___) -> {
                    client.shutdown();
                    done.countDown();
                });

        done.await(30, TimeUnit.SECONDS);
    }

    /**
     * Resolve the user's referral choice asynchronously. In a real host UI this
     * opens a dialog and waits for input; here we read an env var and validate
     * it before applying.
     */
    private static CompletableFuture<ReferralChoice> resolveReferralChoice(FernanClient client) {
        String userPicked = System.getenv("REFERRAL_CODE");
        if (userPicked == null || userPicked.isBlank()) {
            return CompletableFuture.completedFuture(ReferralChoice.none());
        }
        return client.store().validateReferral(userPicked).thenApply(v -> {
            if (!v.valid()) {
                System.out.println("Referral '" + userPicked + "' is not valid; proceeding without one.");
                return ReferralChoice.none();
            }
            System.out.println("Referral '" + userPicked + "' applies " + v.discountPercent() + "% off");
            return ReferralChoice.of(userPicked);
        });
    }

    private static void handleFailure(Throwable t) {
        Throwable cause = t instanceof CompletionException ? t.getCause() : t;
        if (cause instanceof FernanException e) {
            String hint =
                    switch (e.getType()) {
                        case INSUFFICIENT_BALANCE -> "Top up the account balance and retry.";
                        case COOLDOWN -> "Cooldown ends at " + e.getCooldownEndsAt();
                        case RATE_LIMITED -> "Retry after " + e.getRetryAfter() + "s";
                        case AUTHENTICATION -> "Bad API key. Regenerate via the dashboard.";
                        case BANNED -> "Account is banned.";
                        case VALIDATION -> "Invalid request: " + e.getMessage();
                        case NOT_FOUND -> "Resource missing.";
                        case CONFLICT -> "Conflicting request.";
                        case SERVER_ERROR -> "Upstream error " + e.getErrorId();
                        case NETWORK, UNKNOWN -> "Transient failure; retry later.";
                    };
            System.err.println(e.getType() + ": " + hint);
            return;
        }
        System.err.println("Unhandled failure: " + (cause == null ? t : cause));
    }

    @SuppressWarnings("unused")
    private static ErrorType _ignore() {
        return ErrorType.UNKNOWN;
    }
}
