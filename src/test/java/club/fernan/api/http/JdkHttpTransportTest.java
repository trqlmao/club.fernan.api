package club.fernan.api.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import club.fernan.api.auth.ApiKeyAuth;
import club.fernan.api.exception.ErrorType;
import club.fernan.api.exception.FernanException;
import java.net.ServerSocket;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;

class JdkHttpTransportTest {

    @Test
    void connection_failure_maps_to_network() throws Exception {
        int closedPort;
        try (ServerSocket socket = new ServerSocket(0)) {
            closedPort = socket.getLocalPort();
        }
        var http = new JdkHttpTransport("http://127.0.0.1:" + closedPort, new ApiKeyAuth("key"), "test", null, 2_000);
        try {
            var thrown = assertThrows(
                    CompletionException.class, () -> http.get("/user/me").join());
            var e = assertInstanceOf(FernanException.class, thrown.getCause());
            assertEquals(ErrorType.NETWORK, e.type());
        } finally {
            http.shutdown();
        }
    }
}
