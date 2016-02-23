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
     * Add a new request route registry.
     */
    public void request(String route, Middleware mw) {
        routes.put(route, mw);
    }

    /**
     * Neptulon {Middleware} interface implementation.
     */
    @Override
    public void middleware(ReqCtx ctx) {
        Middleware mw = routes.get(ctx.getMethod());
        if (mw != null) {
            mw.middleware(ctx);
            return;
        }

        ctx.next();
    }
}
