package titan.client.middleware;

import neptulon.client.Middleware;
import neptulon.client.ReqCtx;
import titan.client.callbacks.ConnCallbacks;
import titan.client.messages.MsgMessage;

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
        MsgMessage[] msgs = ctx.getParams(MsgMessage[].class);
        try {
            cbs.messagesReceived(msgs);
            ctx.setResponse("ACK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ctx.next();
    }
}
