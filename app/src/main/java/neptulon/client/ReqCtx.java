package neptulon.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.List;
import java.util.logging.Logger;

/**
 * Request context.
 */
public class ReqCtx {
    private static final Logger logger = Logger.getLogger(ReqCtx.class.getSimpleName());
    private final ConnImpl conn;
    private final String id;
    private final String method;
    private final JsonElement params;
    private final List<Middleware> middleware;
    private int mwIndex;
    private final Gson gson;
    private Response response;

    public ReqCtx(ConnImpl conn, String id, String method, JsonElement params, List<Middleware> middleware, Gson gson) {
        if (conn == null) {
            throw new IllegalArgumentException("conn cannot be null");
        }
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (method == null || method.isEmpty()) {
            throw new IllegalArgumentException("method cannot be null or empty");
        }
        if (middleware == null) {
            throw new IllegalArgumentException("middleware cannot be null");
        }
        if (gson == null) {
            throw new IllegalArgumentException("gson cannot be null");
        }

        this.conn = conn;
        this.id = id;
        this.method = method;
        this.params = params;
        this.middleware = middleware;
        this.gson = gson;
    }

    public String getID() {
        return id;
    }

    public String getMethod() {
        return method;
    }

    public <T> T getParams(Class<T> classOfT) {
        return gson.fromJson(params, classOfT);
    }

    public synchronized Response getResponse() {
        return response;
    }

    public synchronized <T> void setResponse(T result) {
        if (response != null) {
            throw new IllegalArgumentException("Response was previously set to: " + response);
        }
        if (result == null) {
            throw new IllegalArgumentException("result cannot be null");
        }
        response = new Response<>(id, result, null);
    }

    public synchronized void setResponseError(Response.ResError err) {
        if (response != null) {
            throw new IllegalArgumentException("Response was previously set to: " + response);
        }
        response = new Response<>(id, null, err);
    }

    public synchronized void next() {
        mwIndex++;

        if (mwIndex <= middleware.size()) {
            middleware.get(mwIndex - 1).middleware(this);
            return;
        }

        if (response != null) {
            conn.send(response);
            return;
        }

        logger.warning("No response provided for for request: " + getID() + ": " + getMethod());
    }
}
