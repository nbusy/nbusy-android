package titan.client;

import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

import neptulon.client.Conn;
import neptulon.client.ConnImpl;
import neptulon.client.ResHandler;
import neptulon.client.Response;
import titan.client.callbacks.Callback;

/**
 * Titan client implementation: https://github.com/titan-x/titan
 */
public class ClientImpl implements Client {
    private static final Logger logger = Logger.getLogger(ClientImpl.class.getSimpleName());
    private final Conn conn;
    private boolean connected;

    public ClientImpl(Conn conn) {
        conn.middleware(new neptulon.client.middleware.Logger());
        this.conn = conn;
    }

    public ClientImpl(String url) {
        this(new ConnImpl(url));
    }

    @Override
    public void connect() {
        conn.connect();
    }

    @Override
    public void jwtAuth(String token, Callback success, Callback fail) {

    }

    @Override
    public void sendMessage(String to, String msg, final Callback sentToServer, final Callback delivered) {
        conn.sendRequest("echo", new Message("", to, new Date(), msg), new ResHandler<String>() {
            @Override
            public Class<String> getType() {
                return String.class;
            }

            @Override
            public void handler(Response<String> res) {
                logger.info("Received response to sendMessage request: " + res.result);
                if (Objects.equals(res.result, "ACK")) {
                    sentToServer.callback();
                    return;
                }
                if (Objects.equals(res.result, "delivered")) {
                    delivered.callback();
                    return;
                }

                logger.info("Received unknown response to sendMessage request: " + res.result);
                close();
            }
        });
    }

    @Override
    public void close() {
        conn.close();
    }
}
