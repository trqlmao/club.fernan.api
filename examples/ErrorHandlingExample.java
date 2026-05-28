import club.fernan.api.FernanClient;
import club.fernan.api.exception.ErrorType;
import club.fernan.api.exception.FernanException;
import club.fernan.api.model.referral.ReferralChoice;
import club.fernan.api.model.store.Purchase;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionException;

/**
 * Demonstrates handling the common transient-error cases — rate limiting,
 * cooldowns, and insufficient balance — with retry-style logic.
 */
public final class ErrorHandlingExample {

    public static void main(String[] args) {
        FernanClient client = FernanClient.builder()
                .apiKey(System.getenv("FERNAN_KEY"))
                .build();

        try {
            Purchase result = attemptPurchase(client, 1, 5);
            System.out.println("Delivered: " + result.deliveredAmount());
        } catch (FernanException e) {
            System.err.println("Gave up: " + e.getType() + " — " + e.getMessage());
        } finally {
            client.shutdown();
        }
    }

    private static Purchase attemptPurchase(FernanClient client, int productId, int qty) {
        try {
            return client.store().purchase(productId, qty, ReferralChoice.none()).join();
        } catch (CompletionException ce) {
            FernanException e = unwrap(ce);
            switch (e.getType()) {
                case RATE_LIMITED -> {
                    long wait = e.getRetryAfter() == null ? 5L : e.getRetryAfter();
                    System.out.println("Rate limited; sleeping " + wait + "s and retrying.");
                    sleep(Duration.ofSeconds(wait));
                    return attemptPurchase(client, productId, qty);
                }
                case COOLDOWN -> {
                    System.out.println("On cooldown until " + e.getCooldownEndsAt() + "; aborting.");
                    throw e;
                }
                case INSUFFICIENT_BALANCE -> {
                    System.out.println("Not enough balance — prompt user to top up.");
                    throw e;
                }
                default -> throw e;
            }
        }
    }

    private static FernanException unwrap(Throwable t) {
        Throwable cause = t instanceof CompletionException ? t.getCause() : t;
        if (cause instanceof FernanException fe) {
            return fe;
        }
        return new FernanException("non-Fernan failure: " + cause, ErrorType.UNKNOWN);
    }

    private static void sleep(Duration d) {
        try {
            Thread.sleep(d.toMillis());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("unused")
    private static Instant now() {
        return Instant.now();
    }
}
