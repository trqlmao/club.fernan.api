package club.fernan.api.http;

import club.fernan.api.exception.ErrorType;
import club.fernan.api.exception.FernanException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.http.HttpResponse;

/**
 * Translates raw HTTP responses into the inner {@code data} JSON object,
 * or throws a categorized {@link FernanException}.
 *
 * <p>The fernan.club API wraps every response in a {@code {success, data}}
 * envelope. On 2xx this returns the unwrapped {@code data} object (or the
 * full body when no envelope is present). On non-2xx it inspects the body
 * to emit the most specific {@link ErrorType}.
 *
 * @author trq
 * @since 0.1.0
 */
public final class ResponseHandler {

    private ResponseHandler() {}

    /**
     * Process the given HTTP response and return the unwrapped data object.
     *
     * @param response The HTTP response to process.
     * @return The {@code data} object on success.
     * @throws FernanException If the response indicates an error.
     */
    public static JsonObject handle(HttpResponse<String> response) {
        int status = response.statusCode();
        String body = response.body();

        JsonObject json;
        try {
            json = JsonParser.parseString(body).getAsJsonObject();
        } catch (Exception e) {
            throw new FernanException("Failed to parse response: " + body, ErrorType.UNKNOWN, status);
        }

        if (status >= 200 && status < 300) {
            if (json.has("data") && json.get("data").isJsonObject()) {
                return json.getAsJsonObject("data");
            }
            return json;
        }

        String error = extractError(json);
        String errorId = json.has("data")
                        && json.get("data").isJsonObject()
                        && json.getAsJsonObject("data").has("error_id")
                ? json.getAsJsonObject("data").get("error_id").getAsString()
                : null;

        throw switch (status) {
            case 400, 422 -> new FernanException(error, ErrorType.VALIDATION, status);
            case 401 -> new FernanException(error, ErrorType.AUTHENTICATION, status);
            case 403 -> handleForbidden(json, error, status);
            case 404 -> new FernanException(error, ErrorType.NOT_FOUND, status);
            case 409 -> new FernanException(error, ErrorType.CONFLICT, status);
            case 429 -> handleRateLimit(json, error, status);
            case 500, 502, 503, 504 -> new FernanException(error, ErrorType.SERVER_ERROR, status, errorId);
            default -> new FernanException(error, ErrorType.UNKNOWN, status);
        };
    }

    private static FernanException handleForbidden(JsonObject json, String error, int status) {
        if (json.has("data") && json.get("data").isJsonObject()) {
            JsonObject data = json.getAsJsonObject("data");
            if (data.has("details")
                    && data.get("details").isJsonObject()
                    && data.getAsJsonObject("details").has("ban_id")) {
                return new FernanException(error, ErrorType.BANNED, status);
            }
        }
        return new FernanException(error, ErrorType.AUTHENTICATION, status);
    }

    private static FernanException handleRateLimit(JsonObject json, String error, int status) {
        if (json.has("data") && json.get("data").isJsonObject()) {
            JsonObject data = json.getAsJsonObject("data");
            if (data.has("cooldown_ends_at")) {
                return new FernanException(
                        error, status, data.get("cooldown_ends_at").getAsString());
            }
            if (data.has("retry_after")) {
                return new FernanException(
                        error, status, data.get("retry_after").getAsLong());
            }
        }
        return new FernanException(error, ErrorType.RATE_LIMITED, status);
    }

    private static String extractError(JsonObject json) {
        if (json.has("data")
                && json.get("data").isJsonObject()
                && json.getAsJsonObject("data").has("error")) {
            return json.getAsJsonObject("data").get("error").getAsString();
        }
        if (json.has("error")) {
            return json.get("error").getAsString();
        }
        return "Unknown error";
    }
}
