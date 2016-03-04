package titan.client;

import java.util.Objects;
import java.util.logging.Logger;

import neptulon.client.Conn;
import neptulon.client.ConnImpl;
import neptulon.client.ResCtx;
import neptulon.client.callbacks.ResCallback;
import neptulon.client.middleware.Router;
import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.EchoCallback;
import titan.client.callbacks.JwtAuthCallback;
import titan.client.callbacks.SendMsgsCallback;
import titan.client.messages.EchoMessage;
import titan.client.messages.JwtAuth;
import titan.client.messages.Message;

/**
 * Titan client implementation: https://github.com/titan-x/titan
 */
public class ClientImpl implements Client {
    private static final Logger logger = Logger.getLogger("Titan: " + ClientImpl.class.getSimpleName());
    private static final String ACK = "ACK";
    private final Router router = new Router();
    private final Conn conn;
    private ConnCallbacks cbs;

    public ClientImpl(Conn conn) {
        if (conn == null) {
            throw new IllegalArgumentException("conn cannot be null");
        }

        conn.middleware(new neptulon.client.middleware.Logger());
        router.request("msg.recv", new RecvMsgsMiddleware(cbs));
        conn.middleware(router);
        this.conn = conn;
    }

    public ClientImpl(String url) {
        this(new ConnImpl(url));
    }

    public ClientImpl() {
        this(new ConnImpl());
    }

    @Override
    public void connect(ConnCallbacks cbs) {
        if (cbs == null) {
            throw new IllegalArgumentException("callbacks cannot be null");
        }

        this.cbs = cbs;
        conn.connect(cbs);
    }

    @Override
    public void jwtAuth(String token, final JwtAuthCallback cb) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("token cannot be null or empty");
        }
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }

        conn.sendRequest("auth.jwt", new JwtAuth(token), new ResCallback() {
            @Override
            public void callback(ResCtx ctx) {
                String res = ctx.getResult(String.class);
                if (!Objects.equals(res, ACK)) {
                    cb.fail();
                } else {
                    cb.success();
                }
            }
        });
    }

    @Override
    public void echo(String msg, final EchoCallback cb) {
        if (msg == null || msg.isEmpty()) {
            throw new IllegalArgumentException("token cannot be null or empty");
        }
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }

        conn.sendRequest("echo", new EchoMessage(msg), new ResCallback() {
            @Override
            public void callback(ResCtx ctx) {
                EchoMessage em = ctx.getResult(EchoMessage.class);
                cb.echoResponse(em.message);
            }
        });
    }

    // todo: send message for singular?
    // todo2: we should set from,date fields for each message ourselves or expect an OutMessage class instead (bonus, variadic!)

    @Override
    public void sendMessages(final SendMsgsCallback cb, Message... msgs) {
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("msgs cannot be null or empty");
        }

        conn.sendRequest("msg.send", msgs, new ResCallback() {
            @Override
            public void callback(ResCtx ctx) {
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
