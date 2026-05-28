/**
 * HTTP transport abstraction.
 *
 * <p>{@link club.fernan.api.http.HttpTransport} is the swappable interface;
 * {@link club.fernan.api.http.JdkHttpTransport} is the default implementation
 * built on {@link java.net.http.HttpClient}.
 * {@link club.fernan.api.http.ResponseHandler} is the static helper that
 * unwraps the {@code {success, data}} envelope and maps non-2xx responses to
 * {@link club.fernan.api.exception.FernanException}.
 *
 * @author trq
 * @since 0.1.0
 */
package club.fernan.api.http;
