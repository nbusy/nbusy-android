package neptulon.client.middleware;

import neptulon.client.Middleware;
import neptulon.client.ReqCtx;
import neptulon.client.Response;

/**
 * Echoes request body as response.
 */
public class Echo implements Middleware {

    @Override
    public void middleware(ReqCtx ctx) {
        ctx.setResponse(ctx.getParams(Object.class));
        ctx.next();
    }
}
