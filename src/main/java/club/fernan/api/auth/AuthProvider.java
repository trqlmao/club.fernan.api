package club.fernan.api.auth;

import java.net.http.HttpRequest;

/**
 * Strategy for authenticating outbound requests to the fernan.club API.
 * Implementations apply headers (and optionally body modifications) to each request.
 *
 * <p>The default implementation is {@link ApiKeyAuth}. Alternative implementations
 * (session cookies, OAuth, signed requests) can be plugged into the builder once
 * the upstream API supports them.
 *
 * @author trq
 * @since 0.1.0
 */
public interface AuthProvider {

    /**
     * Applies authentication to the given request builder.
     *
     * @param request The request builder to mutate.
     */
    void apply(HttpRequest.Builder request);
}
