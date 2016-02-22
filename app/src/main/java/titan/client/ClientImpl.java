package titan.client;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import neptulon.client.Conn;
import neptulon.client.ResCtx;
import neptulon.client.callbacks.ConnCallback;
import neptulon.client.ConnImpl;
import neptulon.client.callbacks.ResCallback;
import titan.client.callbacks.JwtAuthCallback;
import titan.client.callbacks.SendMessageCallback;
import titan.client.messages.JwtAuth;
import titan.client.messages.Message;

/**
 * Titan client implementation: https://github.com/titan-x/titan
 */
public class ClientImpl implements Client {
    private static final Logger logger = Logger.getLogger(ClientImpl.class.getSimpleName());
    private static final String ACK = "ACK";
    private final Conn conn;

    public ClientImpl(Conn conn) {
        conn.middleware(new neptulon.client.middleware.Logger());
        this.conn = conn;
    }

    public ClientImpl(String url) {
        this(new ConnImpl(url));
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

    @Override
    public void sendMessages(List<Message> messages, final SendMessageCallback cb) {
        conn.sendRequest("msg.send", messages, new ResCallback() {
            @Override
            public void handleResponse(ResCtx ctx) {
                String res = ctx.getResult(String.class);

                logger.info("Received response to sendMessage request: " + res);
                if (Objects.equals(res, "ACK")) {
                    cb.sentToServer();
                    return;
                }
                if (Objects.equals(res, "delivered")) {
                    cb.delivered();
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
