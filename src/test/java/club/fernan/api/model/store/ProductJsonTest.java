package club.fernan.api.model.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class ProductJsonTest {

    private final Gson gson = new Gson();

    @Test
    void deserializes_full_product() {
        String json =
                """
                {
                  "product_id": 1,
                  "product_name": "Hypixel Alts",
                  "product_description": "Aged Hypixel accounts",
                  "category": "hypixel",
                  "server_category": "hypixel",
                  "count": 42,
                  "price": 250,
                  "cooldown": 60,
                  "purchase_limit": 100,
                  "image_url": "https://cdn.example.test/p/1.png"
                }
                """;
        Product p = gson.fromJson(json, Product.class);
        assertEquals(1, p.productId());
        assertEquals("Hypixel Alts", p.productName());
        assertEquals("Aged Hypixel accounts", p.productDescription());
        assertEquals("hypixel", p.category());
        assertEquals("hypixel", p.serverCategory());
        assertEquals(42, p.count());
        assertEquals(250, p.price());
        assertEquals(60, p.cooldown());
        assertEquals(100, p.purchaseLimit());
        assertEquals("https://cdn.example.test/p/1.png", p.imageUrl());
        assertTrue(p.inStock());
        assertEquals(2_500L, p.calculateCost(10));
    }

    @Test
    void out_of_stock_helper() {
        String json =
                """
                {"product_id":1,"product_name":"X","category":"x","count":0,"price":1,"cooldown":0,"purchase_limit":0}
                """;
        Product p = gson.fromJson(json, Product.class);
        assertFalse(p.inStock());
    }

    @Test
    void round_trip_preserves_fields() {
        Product original = new Product(7, "Item", "Desc", "cat", "scat", 5, 1000, 30, 50, "https://i.test/7");
        Product reparsed = gson.fromJson(gson.toJson(original), Product.class);
        assertEquals(original, reparsed);
        assertNotNull(reparsed.imageUrl());
    }
}
