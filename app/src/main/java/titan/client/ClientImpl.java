package titan.client;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import neptulon.client.Conn;
import neptulon.client.ConnImpl;
import neptulon.client.ResCtx;
import neptulon.client.callbacks.ConnCallback;
import neptulon.client.callbacks.ResCallback;
import neptulon.client.middleware.Router;
import titan.client.callbacks.JwtAuthCallback;
import titan.client.callbacks.RecvMsgsCallback;
import titan.client.callbacks.SendMsgCallback;
import titan.client.messages.JwtAuth;
import titan.client.messages.Message;

/**
 * Titan client implementation: https://github.com/titan-x/titan
 */
public class ClientImpl implements Client {
    private static final Logger logger = Logger.getLogger(ClientImpl.class.getSimpleName());
    private static final String ACK = "ACK";
    private final Router router = new Router();
    private final Conn conn;

    public ClientImpl(Conn conn, RecvMsgsCallback cb) {
        conn.middleware(new neptulon.client.middleware.Logger());
        router.request("msg.recv", new RecvMsgsMiddleware(cb));
        conn.middleware(router);
        this.conn = conn;
    }

    public ClientImpl(String url, RecvMsgsCallback cb) {
        this(new ConnImpl(url), cb);
    }

    @Override
    public void connect(ConnCallback handler) {
        conn.connect(handler);
    }

    @Override
    public void jwtAuth(String token, final JwtAuthCallback cb) {
        conn.sendRequest("auth.jwt", new JwtAuth(token), new ResCallback() {
            @Override
            public void handleResponse(ResCtx ctx) {
                String res = ctx.getResult(String.class);
                if (!Objects.equals(res, ACK)) {
                    cb.fail();
                } else {
                    cb.success();
                }
            }
        });
    }

    // todo: send message for singular?
    // todo2: we should set from,date fields for each message ourselves or expect an OutMessage class instead (bonus, variadic!)

    @Override
    public void sendMessages(Message[] msgs, final SendMsgCallback cb) {
        conn.sendRequest("msg.send", msgs, new ResCallback() {
            @Override
            public void handleResponse(ResCtx ctx) {
                String res = ctx.getResult(String.class);

                logger.info("Received response to sendMessage request: " + res);
                if (Objects.equals(res, "ACK")) {
                    cb.sentToServer();
                    return;
                }

                logger.info("Received unknown response to sendMessage request: " + res);
                close();
            }
        });
    }

    @Override
    public void close() {
        conn.close();
    }
}
