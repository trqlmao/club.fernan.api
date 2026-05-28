/**
 * Public entry point of the fernan.club Java client.
 *
 * <p>The {@link club.fernan.api.FernanClient} class is the main entry point;
 * build one via {@link club.fernan.api.FernanClient#builder()} and access
 * endpoints through the typed services exposed by the client.
 *
 * <p>All endpoints are asynchronous and return
 * {@link java.util.concurrent.CompletableFuture}. Errors surface as
 * {@link club.fernan.api.exception.FernanException}.
 *
 * @author trq
 * @since 0.1.0
 */
package club.fernan.api;
