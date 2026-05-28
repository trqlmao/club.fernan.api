package club.fernan.api.model.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class RedemptionResultJsonTest {

    @Test
    void computes_actual_added() {
        String json =
                """
                {"message":"ok","value":500,"balance_before":1000,"balance_after":1500}
                """;
        RedemptionResult r = new Gson().fromJson(json, RedemptionResult.class);
        assertEquals(500L, r.actualAdded());
    }
}
