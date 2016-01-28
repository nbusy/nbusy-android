package neptulon.client.middleware;

import neptulon.client.Middleware;
import neptulon.client.ReqCtx;
import neptulon.client.Response;

/**
 * Echoes request body as response.
 */
public class Echo implements Middleware {

    @Override
    public void handler(ReqCtx ctx) {
        ctx.response = new Response<>(ctx.getID(), ctx.getParams(Object.class), null);
    }
}
