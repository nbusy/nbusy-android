package titan.client;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import neptulon.client.Conn;
import neptulon.client.ConnImpl;
import neptulon.client.ResCtx;
import neptulon.client.callbacks.ResCallback;
import titan.client.callbacks.GoogleAuthCallback;
import titan.client.middleware.RecvMsgsMiddleware;
import titan.client.callbacks.ConnCallbacks;
import titan.client.callbacks.EchoCallback;
import titan.client.callbacks.JWTAuthCallback;
import titan.client.callbacks.SendMsgsCallback;
import titan.client.messages.EchoMessage;
import titan.client.messages.TokenMessage;
import titan.client.messages.MsgMessage;
import titan.client.responses.GoogleAuthResponse;

/**
 * Titan client implementation: https://github.com/titan-x/titan
 */
public class ClientImpl implements Client {
    private static final Logger logger = Logger.getLogger("Titan: " + ClientImpl.class.getSimpleName());
    private static final String ACK = "ACK";
    private final Conn conn;
    private final AtomicReference<ConnCallbacks> cbs = new AtomicReference<>();

    public ClientImpl(Conn conn) {
        if (conn == null) {
            throw new IllegalArgumentException("conn cannot be null");
        }

        this.conn = conn;
    }

    public ClientImpl(String url, boolean async) {
        this(new ConnImpl(url, async));
    }

    public ClientImpl() {
        this(new ConnImpl());
    }

    private boolean ensureConn() {
        if (!conn.isConnected()) {
            if (cbs.get() == null) {
                throw new IllegalStateException("connect has never been called to initiate the connection");
            }

            conn.connect(cbs.get());
            return false;
        }

        return true;
    }

    /********************
     * Client Overrides *
     ********************/

    @Override
    public synchronized void connect(ConnCallbacks cbs) {
        if (cbs == null) {
            throw new IllegalArgumentException("callbacks cannot be null");
        }
        if (this.cbs.get() == null) {
            conn.handleRequest("msg.recv", new RecvMsgsMiddleware(cbs));
        }

        this.cbs.set(cbs);
        conn.connect(cbs);
    }

    @Override
    public synchronized boolean isConnected() {
        return conn.isConnected();
    }

    @Override
    public boolean haveOngoingRequests() {
        return conn.haveOngoingRequests();
    }

    @Override
    public boolean googleAuth(String token, final GoogleAuthCallback cb) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("token cannot be null or empty");
        }
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        if (!ensureConn()) {
            return false;
        }

        conn.sendRequest( "auth.google", new TokenMessage(token), new ResCallback() {
            @Override
            public void callback(ResCtx ctx) {
                GoogleAuthResponse res = ctx.getResult(GoogleAuthResponse.class);
                if (ctx.isSuccess) {
                    cb.success(res);
                } else {
                    cb.fail(ctx.errorCode, ctx.errorMessage);
                }
            }
        });
        return true;
    }

    @Override
    public boolean jwtAuth(String token, final JWTAuthCallback cb) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("token cannot be null or empty");
        }
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        if (!ensureConn()) {
            return false;
        }

        conn.sendRequest("auth.jwt", new TokenMessage(token), new ResCallback() {
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
        return true;
    }

    @Override
    public boolean echo(String msg, final EchoCallback cb) {
        if (msg == null || msg.isEmpty()) {
            throw new IllegalArgumentException("message cannot be null or empty");
        }
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        if (!ensureConn()) {
            return false;
        }

        conn.sendRequest("echo", new EchoMessage(msg), new ResCallback() {
            @Override
            public void callback(ResCtx ctx) {
                EchoMessage em = ctx.getResult(EchoMessage.class);
                cb.echoResponse(em.message);
            }
        });
        return true;
    }

    // todo: send message for singular?
    // todo2: we should set from,date fields for each message ourselves or expect an OutMessage class instead (bonus, variadic!)

    @Override
    public boolean sendMessages(final SendMsgsCallback cb, MsgMessage... msgs) {
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("at least one message must be provided for delivery");
        }
        if (!ensureConn()) {
            return false;
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
        return true;
    }

    @Override
    public synchronized void close() {
        conn.close();
    }
}
