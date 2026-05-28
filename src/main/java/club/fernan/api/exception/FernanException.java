package club.fernan.api.exception;

/**
 * Unified exception for all fernan.club API errors.
 * Carries an {@link ErrorType} plus optional HTTP status and rate-limit metadata.
 *
 * <p>Prefer switching on {@link #getType()} over inspecting the HTTP status code.
 *
 * @author trq
 * @since 0.1.0
 */
public class FernanException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorType type;
    private final int statusCode;
    private final String errorId;
    private final Long retryAfter;
    private final String cooldownEndsAt;

    public FernanException(String message, ErrorType type) {
        super(message);
        this.type = type;
        this.statusCode = -1;
        this.errorId = null;
        this.retryAfter = null;
        this.cooldownEndsAt = null;
    }

    public FernanException(String message, ErrorType type, Throwable cause) {
        super(message, cause);
        this.type = type;
        this.statusCode = -1;
        this.errorId = null;
        this.retryAfter = null;
        this.cooldownEndsAt = null;
    }

    public FernanException(String message, ErrorType type, int statusCode) {
        super(message);
        this.type = type;
        this.statusCode = statusCode;
        this.errorId = null;
        this.retryAfter = null;
        this.cooldownEndsAt = null;
    }

    public FernanException(String message, ErrorType type, int statusCode, String errorId) {
        super(message);
        this.type = type;
        this.statusCode = statusCode;
        this.errorId = errorId;
        this.retryAfter = null;
        this.cooldownEndsAt = null;
    }

    /** Rate-limit exception with {@code retry_after} seconds. */
    public FernanException(String message, int statusCode, long retryAfter) {
        super(message);
        this.type = ErrorType.RATE_LIMITED;
        this.statusCode = statusCode;
        this.errorId = null;
        this.retryAfter = retryAfter;
        this.cooldownEndsAt = null;
    }

    /** Cooldown exception with ISO-8601 {@code cooldown_ends_at} timestamp. */
    public FernanException(String message, int statusCode, String cooldownEndsAt) {
        super(message);
        this.type = ErrorType.COOLDOWN;
        this.statusCode = statusCode;
        this.errorId = null;
        this.retryAfter = null;
        this.cooldownEndsAt = cooldownEndsAt;
    }

    public ErrorType getType() {
        return type;
    }

    /** HTTP status code, or {@code -1} if not applicable (e.g. network error). */
    public int getStatusCode() {
        return statusCode;
    }

    /** Server-generated error ID for 5xx errors, or {@code null}. */
    public String getErrorId() {
        return errorId;
    }

    /** Seconds until the rate limit resets, or {@code null}. */
    public Long getRetryAfter() {
        return retryAfter;
    }

    /** ISO-8601 timestamp when the cooldown expires, or {@code null}. */
    public String getCooldownEndsAt() {
        return cooldownEndsAt;
    }
}
