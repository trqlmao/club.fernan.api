package club.fernan.api.exception;

/**
 * Categorizes failure modes returned by the fernan.club API.
 * Consumers should switch on this enum rather than parsing HTTP status codes directly.
 *
 * @author trq
 * @since 0.1.0
 */
public enum ErrorType {

    /** Network or connection failure (no HTTP response received). */
    NETWORK,

    /** Invalid API key or insufficient permissions for the requested resource. */
    AUTHENTICATION,

    /** Account is banned. The exception may carry ban details in its message. */
    BANNED,

    /** Request validation failed (HTTP 400 or 422). */
    VALIDATION,

    /** Requested resource was not found (HTTP 404). */
    NOT_FOUND,

    /** Insufficient balance to complete the purchase. */
    INSUFFICIENT_BALANCE,

    /** Product purchase cooldown is currently active. See {@code cooldownEndsAt}. */
    COOLDOWN,

    /** Rate limit exceeded. See {@code retryAfter}. */
    RATE_LIMITED,

    /** Resource conflict, e.g. a duplicate refund already exists (HTTP 409). */
    CONFLICT,

    /** Server-side error (HTTP 5xx). May include {@code errorId} for support. */
    SERVER_ERROR,

    /** Unknown or unclassified error. Inspect the raw message for details. */
    UNKNOWN
}
