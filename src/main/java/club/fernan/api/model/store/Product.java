package club.fernan.api.model.store;

import com.google.gson.annotations.SerializedName;

/**
 * A product available for purchase in the fernan.club store.
 *
 * @author trq
 * @since 0.1.0
 */
public record Product(
        @SerializedName("product_id") int productId,
        @SerializedName("product_name") String productName,
        @SerializedName("product_description") String productDescription,
        String category,
        @SerializedName("server_category") String serverCategory,
        int count,
        int price,
        int cooldown,
        @SerializedName("purchase_limit") int purchaseLimit,
        @SerializedName("image_url") String imageUrl) {

    /** True if {@code count > 0}. */
    public boolean inStock() {
        return count > 0;
    }

    /** Total cost in pesos for the requested amount. */
    public long calculateCost(int amount) {
        return (long) price * amount;
    }
}
