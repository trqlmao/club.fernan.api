package club.fernan.api.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Result of redeeming a peso key.
 *
 * @author trq
 * @since 0.1.0
 */
public record RedemptionResult(
        String message,
        long value,
        @SerializedName("balance_before") long balanceBefore,
        @SerializedName("balance_after") long balanceAfter) {

    /** Difference between {@code balance_after} and {@code balance_before}. */
    public long actualAdded() {
        return balanceAfter - balanceBefore;
    }
}
