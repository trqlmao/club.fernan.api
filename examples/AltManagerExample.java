import club.fernan.api.FernanClient;
import club.fernan.api.exception.ErrorType;
import club.fernan.api.exception.FernanException;
import club.fernan.api.model.referral.ReferralChoice;
import club.fernan.api.model.referral.ReferralValidation;
import club.fernan.api.model.store.Product;
import club.fernan.api.model.store.Purchase;
import club.fernan.api.model.store.PurchasedAccount;
import java.util.List;
import java.util.concurrent.CompletionException;

/**
 * Canonical reference for wiring this library into a client's alt-storage layer.
 *
 * <p>Defines a tiny {@link AltStore} interface representing whatever your host
 * application uses to remember credentials. Walks through the typical flow:
 *
 * <ol>
 *   <li>List available stock for the user.</li>
 *   <li>Ask the user which referral code (if any) to apply.</li>
 *   <li>Validate the referral code so the user sees the discount before paying.</li>
 *   <li>Issue the purchase.</li>
 *   <li>For each delivered account, decode the session blob and register it with
 *       your alt store.</li>
 * </ol>
 *
 * <p>This file is illustrative — drop the pieces you need into your real app
 * rather than copying it whole.
 */
public final class AltManagerExample {

    /**
     * Plug-point for the host application's credential store. Anything that
     * accepts {@code (username, uuid, sessionBlob)} qualifies.
     */
    public interface AltStore {
        void register(String username, String uuid, String sessionBlob);
    }

    public static void main(String[] args) {
        FernanClient client = FernanClient.builder()
                .apiKey(System.getenv("FERNAN_KEY"))
                .userAgent("my-mod/1.0")
                .integration("my-mod")
                .build();

        AltStore altStore = (u, id, blob) -> System.out.println("Stored " + u + " (" + id + ")");

        try {
            // 1. Browse stock.
            List<Product> stock = client.store().getStock().join();
            Product chosen = stock.stream()
                    .filter(Product::inStock)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No stock available"));
            System.out.println("Buying: " + chosen.productName() + " @ " + chosen.price());

            // 2. Build the referral choice. NEVER default this silently;
            //    your UI must surface it to the user.
            ReferralChoice referral = promptForReferralChoice(client);

            // 3. Issue the purchase.
            int quantity = 1;
            Purchase result = client.store()
                    .purchase(chosen.productId(), quantity, referral)
                    .join();

            // 4. Hand off each delivered account to the host's alt store.
            for (PurchasedAccount account : result.products()) {
                altStore.register(account.username(), account.formattedUuid(), account.decodedData());
            }

            System.out.println(
                    "Delivered " + result.deliveredAmount() + "/" + result.requestedAmount());
        } catch (CompletionException ce) {
            handleFailure(ce);
        } finally {
            client.shutdown();
        }
    }

    /**
     * Stand-in for whatever your host UI uses to ask the user. In a real app
     * this opens a dialog and waits for input; here we just demonstrate the
     * validation flow.
     */
    private static ReferralChoice promptForReferralChoice(FernanClient client) {
        String userPicked = System.getenv("REFERRAL_CODE");
        if (userPicked == null || userPicked.isBlank()) {
            return ReferralChoice.none();
        }
        ReferralValidation v = client.store().validateReferral(userPicked).join();
        if (!v.valid()) {
            System.out.println("Referral '" + userPicked + "' is not valid; proceeding without one.");
            return ReferralChoice.none();
        }
        System.out.println("Referral '" + userPicked + "' applies " + v.discountPercent() + "% off");
        return ReferralChoice.of(userPicked);
    }

    private static void handleFailure(CompletionException ce) {
        Throwable cause = ce.getCause();
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
        if (cause == null) {
            throw new RuntimeException("Unknown failure", ce);
        }
        throw new RuntimeException(cause);
    }

    @SuppressWarnings("unused")
    private static ErrorType _ignore() {
        return ErrorType.UNKNOWN;
    }
}
