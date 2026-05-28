/**
 * Error reporting. A single {@link club.fernan.api.exception.FernanException}
 * carries a typed {@link club.fernan.api.exception.ErrorType} plus optional
 * HTTP status, retry-after, and cooldown metadata.
 *
 * <p>Consumers should switch on {@code ErrorType} rather than inspecting raw
 * HTTP status codes.
 *
 * @author trq
 * @since 0.1.0
 */
package club.fernan.api.exception;
