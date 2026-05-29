package club.fernan.api.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import club.fernan.api.exception.ErrorType;
import club.fernan.api.exception.FernanException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.net.ssl.SSLSession;
import org.junit.jupiter.api.Test;

class ResponseHandlerTest {

    @Test
    void unwraps_2xx_envelope() {
        var response = fakeResponse(200, "{\"success\":true,\"data\":{\"hello\":\"world\"}}");
        var data = ResponseHandler.handle(response);
        assertEquals("world", data.get("hello").getAsString());
    }

    @Test
    void returns_full_body_when_no_envelope() {
        var response = fakeResponse(200, "{\"hello\":\"world\"}");
        var data = ResponseHandler.handle(response);
        assertEquals("world", data.get("hello").getAsString());
    }

    @Test
    void unparseable_body_throws_unknown() {
        var response = fakeResponse(200, "<html>not json</html>");
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.UNKNOWN, ex.type());
    }

    @Test
    void status_400_maps_to_validation() {
        var response = fakeResponse(400, "{\"success\":false,\"data\":{\"error\":\"bad amount\"}}");
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.VALIDATION, ex.type());
        assertEquals(400, ex.statusCode());
        assertEquals("bad amount", ex.getMessage());
    }

    @Test
    void status_422_maps_to_validation() {
        var response = fakeResponse(422, "{\"success\":false,\"data\":{\"error\":\"unprocessable\"}}");
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.VALIDATION, ex.type());
    }

    @Test
    void status_401_maps_to_authentication() {
        var response = fakeResponse(401, "{\"success\":false,\"data\":{\"error\":\"invalid key\"}}");
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.AUTHENTICATION, ex.type());
    }

    @Test
    void status_403_with_ban_id_maps_to_banned() {
        var body = "{\"success\":false,\"data\":{\"error\":\"banned\",\"details\":{\"ban_id\":\"abc-123\"}}}";
        var response = fakeResponse(403, body);
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.BANNED, ex.type());
    }

    @Test
    void status_403_without_ban_id_maps_to_authentication() {
        var response = fakeResponse(403, "{\"success\":false,\"data\":{\"error\":\"forbidden\"}}");
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.AUTHENTICATION, ex.type());
    }

    @Test
    void status_404_maps_to_not_found() {
        var response = fakeResponse(404, "{\"success\":false,\"data\":{\"error\":\"missing\"}}");
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.NOT_FOUND, ex.type());
    }

    @Test
    void status_409_maps_to_conflict() {
        var response = fakeResponse(409, "{\"success\":false,\"data\":{\"error\":\"dup\"}}");
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.CONFLICT, ex.type());
    }

    @Test
    void status_429_with_cooldown_maps_to_cooldown() {
        var body = "{\"success\":false,\"data\":{\"error\":\"cd\",\"cooldown_ends_at\":\"2026-05-28T12:00:00Z\"}}";
        var response = fakeResponse(429, body);
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.COOLDOWN, ex.type());
        assertEquals("2026-05-28T12:00:00Z", ex.cooldownEndsAt());
    }

    @Test
    void status_429_with_retry_after_maps_to_rate_limited() {
        var body = "{\"success\":false,\"data\":{\"error\":\"rl\",\"retry_after\":42}}";
        var response = fakeResponse(429, body);
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.RATE_LIMITED, ex.type());
        assertEquals(42L, ex.retryAfter());
    }

    @Test
    void status_429_bare_maps_to_rate_limited() {
        var response = fakeResponse(429, "{\"success\":false,\"data\":{\"error\":\"rl\"}}");
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.RATE_LIMITED, ex.type());
    }

    @Test
    void status_500_maps_to_server_error_with_error_id() {
        var body = "{\"success\":false,\"data\":{\"error\":\"boom\",\"error_id\":\"err-9001\"}}";
        var response = fakeResponse(500, body);
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.SERVER_ERROR, ex.type());
        assertEquals("err-9001", ex.errorId());
    }

    @Test
    void unknown_status_maps_to_unknown() {
        var response = fakeResponse(418, "{\"success\":false,\"data\":{\"error\":\"teapot\"}}");
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertEquals(ErrorType.UNKNOWN, ex.type());
    }

    @Test
    void missing_error_field_falls_back_to_unknown_error_message() {
        var response = fakeResponse(400, "{\"success\":false,\"data\":{}}");
        var ex = assertThrows(FernanException.class, () -> ResponseHandler.handle(response));
        assertNotNull(ex.getMessage());
    }

    private static HttpResponse<String> fakeResponse(int status, String body) {
        return new HttpResponse<>() {
            @Override
            public int statusCode() {
                return status;
            }

            @Override
            public HttpRequest request() {
                return HttpRequest.newBuilder(URI.create("https://example.test/"))
                        .build();
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return HttpHeaders.of(Map.of(), (a, b) -> true);
            }

            @Override
            public String body() {
                return body;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return URI.create("https://example.test/");
            }

            @Override
            public HttpClient.Version version() {
                return HttpClient.Version.HTTP_2;
            }
        };
    }

    @SuppressWarnings("unused")
    private static List<String> headerList(String key) {
        return List.of();
    }
}
