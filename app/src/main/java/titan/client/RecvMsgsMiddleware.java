package titan.client;

import neptulon.client.Middleware;
import neptulon.client.ReqCtx;
import titan.client.callbacks.ConnCallbacks;
import titan.client.messages.Message;

/**
 * Incoming messages handler middleware.
 */
public class RecvMsgsMiddleware implements Middleware {
    private final ConnCallbacks cbs;

    public RecvMsgsMiddleware(ConnCallbacks cbs) {
        this.cbs = cbs;
    }

    @Override
    public void middleware(ReqCtx ctx) {
        Message[] msgs = ctx.getParams(Message[].class);
        ctx.next();
        cbs.messagesReceived(msgs);
    }
}