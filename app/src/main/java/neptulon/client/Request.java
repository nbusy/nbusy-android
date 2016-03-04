package neptulon.client;

/**
 * JSON-RPC request object.
 */
public class Request<T> {
    final String id;
    final String method;
    final T params;

    Request(String id, String method, T params) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (method == null || method.isEmpty()) {
            throw new IllegalArgumentException("method cannot be null or empty");
        }

        this.id = id;
        this.method = method;
        this.params = params;
    }
}
