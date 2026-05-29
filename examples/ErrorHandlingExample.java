import club.fernan.api.FernanClient;
import club.fernan.api.exception.ErrorType;
import club.fernan.api.exception.FernanException;
import club.fernan.api.model.referral.ReferralChoice;
import club.fernan.api.model.store.Purchase;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates handling the common transient-error cases — rate limiting,
 * cooldowns, and insufficient balance — with async retry chaining.
 *
 * <p>This uses {@code .handle} + a scheduled retry rather than blocking
 * {@code Thread.sleep}, so the host application's threads stay free.
 */
public final class ErrorHandlingExample {

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "retry-scheduler");
        t.setDaemon(true);
        return t;
    });

    public static void main(String[] args) throws InterruptedException {
        FernanClient client =
                FernanClient.builder().apiKey(System.getenv("FERNAN_KEY")).build();

        CountDownLatch done = new CountDownLatch(1);

        attemptPurchase(client, 1, 5)
                .thenAccept(p -> System.out.println("Delivered: " + p.deliveredAmount()))
                .exceptionally(t -> {
                    FernanException e = unwrap(t);
                    System.err.println("Gave up: " + e.type() + " — " + e.getMessage());
                    return null;
                })
                .whenComplete((__, ___) -> {
                    client.shutdown();
                    SCHEDULER.shutdown();
                    done.countDown();
                });

        done.await(60, TimeUnit.SECONDS);
    }

    private static CompletableFuture<Purchase> attemptPurchase(FernanClient client, int productId, int qty) {
        return client.store()
                .purchase(productId, qty, ReferralChoice.none())
                .handle((p, t) -> {
                    if (t == null) {
                        return CompletableFuture.completedFuture(p);
                    }
                    FernanException e = unwrap(t);
                    return switch (e.type()) {
                        case RATE_LIMITED -> {
                            long wait = e.retryAfter() == null ? 5L : e.retryAfter();
                            System.out.println("Rate limited; retrying in " + wait + "s.");
                            yield delayed(Duration.ofSeconds(wait))
                                    .thenCompose(__ -> attemptPurchase(client, productId, qty));
                        }
                        case COOLDOWN -> {
                            System.out.println("On cooldown until " + e.cooldownEndsAt() + "; aborting.");
                            yield CompletableFuture.<Purchase>failedFuture(e);
                        }
                        case INSUFFICIENT_BALANCE -> {
                            System.out.println("Not enough balance — prompt user to top up.");
                            yield CompletableFuture.<Purchase>failedFuture(e);
                        }
                        default -> CompletableFuture.<Purchase>failedFuture(e);
                    };
                })
                .thenCompose(f -> f);
    }

    private static CompletableFuture<Void> delayed(Duration d) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        SCHEDULER.schedule(() -> future.complete(null), d.toMillis(), TimeUnit.MILLISECONDS);
        return future;
    }

    private static FernanException unwrap(Throwable t) {
        Throwable cause = t instanceof CompletionException ? t.getCause() : t;
        if (cause instanceof FernanException fe) {
            return fe;
        }
        return new FernanException("non-Fernan failure: " + cause, ErrorType.UNKNOWN);
    }
}
