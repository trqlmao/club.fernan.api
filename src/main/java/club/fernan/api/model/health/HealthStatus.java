package club.fernan.api.model.health;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Health status reported by {@code /health}.
 *
 * @author trq
 * @since 0.1.0
 */
public record HealthStatus(
        @SerializedName("status") String status, @SerializedName("services") Map<String, String> services) {

    public boolean isHealthy() {
        return "healthy".equalsIgnoreCase(status);
    }

    public boolean isDegraded() {
        return "degraded".equalsIgnoreCase(status);
    }

    /** True if the named dependency reports {@code "healthy"}. */
    public boolean isServiceHealthy(String serviceName) {
        if (services == null) {
            return false;
        }
        return "healthy".equalsIgnoreCase(services.get(serviceName));
    }
}
