package club.fernan.api.model.store;

import com.google.gson.annotations.SerializedName;
import java.util.Base64;

/**
 * A single account delivered as part of a {@link Purchase}.
 * The {@code data} field is Base64-encoded session/cookie data.
 *
 * @author trq
 * @since 0.1.0
 */
public record PurchasedAccount(
        @SerializedName("data") String data,
        @SerializedName("username") String username,
        @SerializedName("uuid") String uuid,
        @SerializedName("access_token") String accessToken) {

    /** Decoded session/cookie data, or {@code null} if {@code data} is empty. */
    public String decodedData() {
        if (data == null || data.isBlank()) {
            return null;
        }
        try {
            return new String(Base64.getDecoder().decode(data));
        } catch (IllegalArgumentException e) {
            return data;
        }
    }

    /** UUID with dashes inserted if not already present. */
    public String formattedUuid() {
        if (uuid == null) {
            return null;
        }
        if (uuid.contains("-")) {
            return uuid;
        }
        return uuid.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
    }
}
