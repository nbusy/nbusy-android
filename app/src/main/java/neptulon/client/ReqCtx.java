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

    public Response response;

    public ReqCtx(ConnImpl conn, String id, String method, JsonElement params, List<Middleware> middleware, Gson gson) {
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

    public void next() {
        mwIndex++;

        if (mwIndex <= middleware.size()) {
            middleware.get(mwIndex - 1).handler(this);
            return;
        }

        if (response != null) {
            conn.send(response);
            return;
        }

        logger.warning("No response provided for for request: " + getID() + ": " + getMethod());
    }
}
