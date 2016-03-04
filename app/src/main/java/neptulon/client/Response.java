package neptulon.client;

/**
 * JSON-RPC response object.
 */
public class Response<T> {
    public final String id;
    public final T result;
    public final ResError error;

    public Response(String id, T result, ResError error) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }

        this.id = id;
        this.result = result;
        this.error = error;
    }

    class ResError<K> {
        final int code;
        final String message;
        final K data;

        ResError(int code, String message, K data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
    }
}
