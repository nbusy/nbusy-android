package titan.client;

import neptulon.client.Middleware;
import neptulon.client.ReqCtx;
import titan.client.callbacks.RecvMsgCallback;

/**
 * Incoming message handler middleware.
 */
public class RecvMsgMiddleware implements Middleware {
    private final RecvMsgCallback cb;

    public RecvMsgMiddleware(RecvMsgCallback cb) {
        this.cb = cb;
    }

    @Override
    public void middleware(ReqCtx ctx) {
// todo: ctx.Params(InMsg)...
    }
}
