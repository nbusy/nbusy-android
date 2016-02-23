package titan.client;

import neptulon.client.Middleware;
import neptulon.client.ReqCtx;
import titan.client.callbacks.RecvMsgsCallback;
import titan.client.messages.Message;

/**
 * Incoming messages handler middleware.
 */
public class RecvMsgsMiddleware implements Middleware {
    private final RecvMsgsCallback cb;

    public RecvMsgsMiddleware(RecvMsgsCallback cb) {
        this.cb = cb;
    }

    @Override
    public void middleware(ReqCtx ctx) {
        Message[] msgs = ctx.getParams(Message[].class);
        ctx.next();
    }
}
