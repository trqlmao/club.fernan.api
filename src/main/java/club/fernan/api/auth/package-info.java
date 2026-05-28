/**
 * Authentication strategies for outbound API requests.
 *
 * <p>The default strategy is {@link club.fernan.api.auth.ApiKeyAuth}, which
 * adds an {@code X-API-Key} header. Custom strategies can be plugged in by
 * implementing {@link club.fernan.api.auth.AuthProvider} once the upstream
 * API supports alternate auth modes.
 *
 * @author trq
 * @since 0.1.0
 */
package club.fernan.api.auth;
