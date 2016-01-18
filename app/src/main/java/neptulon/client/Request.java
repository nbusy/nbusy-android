package neptulon.client;

/**
 * JSON-RPC request object.
 */
public class Request<T> {
    final String id;
    final String method;
    final T params;

    Request(String id, String method, T params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }
}
