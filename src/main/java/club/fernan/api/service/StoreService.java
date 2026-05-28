package club.fernan.api.service;

import club.fernan.api.http.JdkHttpTransport;
import club.fernan.api.locale.FernanLocale;
import club.fernan.api.model.referral.ReferralChoice;
import club.fernan.api.model.referral.ReferralValidation;
import club.fernan.api.model.store.Cooldown;
import club.fernan.api.model.store.Product;
import club.fernan.api.model.store.Purchase;
import club.fernan.api.model.store.PurchaseDetail;
import club.fernan.api.model.store.PurchaseHistory;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Store-facing endpoints: stock, purchases, cooldowns, referral validation.
 *
 * @author trq
 * @since 0.1.0
 */
public final class StoreService {

    private final JdkHttpTransport http;

    public StoreService(JdkHttpTransport http) {
        this.http = http;
    }

    /** List available products. */
    public CompletableFuture<List<Product>> getStock() {
        return getStock(null);
    }

    /** List available products in the requested locale. */
    public CompletableFuture<List<Product>> getStock(FernanLocale locale) {
        return http.get("/store/stock" + localeQuery(locale)).thenApply(data -> {
            JsonArray arr = data.getAsJsonArray("stock");
            return http.gson().fromJson(arr, new TypeToken<List<Product>>() {}.getType());
        });
    }

    /** List per-product cooldown/purchase-limit status. */
    public CompletableFuture<List<Cooldown>> getCooldowns() {
        return getCooldowns(null);
    }

    /** List per-product cooldown/purchase-limit status in the requested locale. */
    public CompletableFuture<List<Cooldown>> getCooldowns(FernanLocale locale) {
        return http.get("/store/cooldown" + localeQuery(locale)).thenApply(data -> {
            JsonArray arr = data.getAsJsonArray("cooldowns");
            return http.gson().fromJson(arr, new TypeToken<List<Cooldown>>() {}.getType());
        });
    }

    /**
     * Purchase accounts. The caller must explicitly choose whether a referral
     * code applies; passing {@link ReferralChoice#none()} declines all referrals.
     *
     * @param productId Product to purchase.
     * @param amount    Quantity (1-100).
     * @param referral  Explicit referral choice (never {@code null}).
     */
    public CompletableFuture<Purchase> purchase(int productId, int amount, ReferralChoice referral) {
        if (referral == null) {
            throw new IllegalArgumentException(
                    "ReferralChoice must be explicit (use ReferralChoice.none() to decline)");
        }
        Map<String, Object> body = new HashMap<>();
        body.put("product_id", productId);
        body.put("amount", amount);
        if (referral.isPresent()) {
            body.put("referral_code", referral.code());
        }
        return http.post("/store/purchase", body).thenApply(data -> http.gson().fromJson(data, Purchase.class));
    }

    /** Purchase history with default pagination ({@code limit=20, offset=0}). */
    public CompletableFuture<List<PurchaseHistory>> getPurchases() {
        return getPurchases(20, 0);
    }

    /** Purchase history with explicit pagination. */
    public CompletableFuture<List<PurchaseHistory>> getPurchases(int limit, int offset) {
        return http.get("/store/purchases?limit=" + limit + "&offset=" + offset).thenApply(data -> {
            JsonArray arr = data.getAsJsonArray("purchases");
            return http.gson().fromJson(arr, new TypeToken<List<PurchaseHistory>>() {}.getType());
        });
    }

    /** Detail view of a single purchase. */
    public CompletableFuture<PurchaseDetail> getPurchase(String purchaseId) {
        return http.get("/store/purchases/" + purchaseId)
                .thenApply(data -> http.gson().fromJson(data.getAsJsonObject("purchase"), PurchaseDetail.class));
    }

    /** Validate a referral code against the server before applying it. */
    public CompletableFuture<ReferralValidation> validateReferral(String code) {
        return http.post("/store/referral/validate", Map.of("code", code))
                .thenApply(data -> http.gson().fromJson(data, ReferralValidation.class));
    }

    private static String localeQuery(FernanLocale locale) {
        return locale == null ? "" : "?locale=" + locale.code();
    }
}
