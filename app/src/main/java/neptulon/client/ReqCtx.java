package neptulon.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.List;

/**
 * Request context.
 */
public class ReqCtx {
    private final String id;
    private final String method;
    private final JsonElement params;
    private final List<Middleware> middleware;
    private int mwIndex;
    private final Gson gson;

    public Response response;

    public ReqCtx(String id, String method, JsonElement params, List<Middleware> middleware, Gson gson) {
        this.id = id;
        this.method = method;
        this.params = params;
        this.middleware = middleware;
        this.gson = gson;
    }

    public <T> T getParams(Class<T> classOfT) {
        return gson.fromJson(params, classOfT);
    }

    public void next() {
        mwIndex++;

        if (mwIndex <= middleware.size()) {
            middleware.get(mwIndex-1).handler(this);
        }
    }
}
