package neptulon.client.middleware;

import neptulon.client.Middleware;
import neptulon.client.ReqCtx;

import java.util.HashMap;
import java.util.Map;

/**
 * Request router middleware.
 */
public class Router implements Middleware {
    private final Map<String, Middleware> routes = new HashMap<>();

    /**
     * Adds a new request route registry.
     */
    public void request(String route, Middleware mw) {
        routes.put(route, mw);
    }

    @Override
    public void handler(ReqCtx ctx) {
        Middleware mw = routes.get(ctx.getMethod());
        if (mw != null) {
            mw.handler(ctx);
        }

        ctx.next();
    }
}
