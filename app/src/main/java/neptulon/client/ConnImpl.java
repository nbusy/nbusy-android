package neptulon.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import neptulon.client.callbacks.ConnCallback;
import neptulon.client.callbacks.ResCallback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;

/**
 * Neptulon connection implementation: https://github.com/neptulon/neptulon
 */
public class ConnImpl implements Conn, WebSocketListener {
    private static final Logger logger = Logger.getLogger("Neptulon: " + ConnImpl.class.getSimpleName());
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();
    private final OkHttpClient client;
    private final Request request;
    private final WebSocketCall wsCall;
    private final List<Middleware> middleware = new ArrayList<>();
    private final Map<String, ResCallback> resCallbacks = new HashMap<>();
    private String ws_url;
    private WebSocket ws;
    private boolean connected;
    private ConnCallback connCallback;

    /**
     * Initializes a new connection with given server URL.
     */
    public ConnImpl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("url cannot be null or empty");
        }

        ws_url = url;
        client = new OkHttpClient.Builder()
                .connectTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        request = new Request.Builder()
                .url(url)
                .build();

        wsCall = WebSocketCall.create(client, request);
    }

    /**
     * Initializes a new connection with default server URL: "ws://10.0.2.2:3000"
     * which connects to "ws://127.0.0.1:3000" on Android emulator host machine.
     */
    public ConnImpl() {
        this("ws://10.0.2.2:3000");
    }

    void send(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }

        String m = gson.toJson(obj);
        logger.info("Outgoing message: " + m);
        try {
            ws.sendMessage(RequestBody.create(WebSocket.TEXT, m));
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    /***********************
     * Conn Implementation *
     ***********************/

    @Override
    public void useTLS(byte[] ca, byte[] clientCert, byte[] clientCertKey) {
        // todo: https://github.com/square/okhttp/wiki/HTTPS
        // * pin and use a single trusted custom server cert
        // * limit ciphers and TLS version
    }

    @Override
    public void setDeadline(int seconds) {

    }

    @Override
    public void middleware(Middleware mw) {
        if (mw == null) {
            throw new IllegalArgumentException("mw cannot be null");
        }

        middleware.add(mw);
    }

    // todo: add default router
    // handleRequest(method, .....) { if isClientConn... else exception } // same goes for go-client

    @Override
    public void connect(ConnCallback cb) {
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }

        // enqueue this listener implementation to initiate the WebSocket connection
        connCallback = cb;
        wsCall.enqueue(this);
    }

    @Override
    public void remoteAddr() {
        if (!connected) {
            throw new IllegalStateException("Not connected.");
        }
    }

    @Override
    public <T> void sendRequest(String method, T params, ResCallback cb) {
        if (!connected) {
            throw new IllegalStateException("Not connected.");
        }
        if (method == null || method.isEmpty()) {
            throw new IllegalArgumentException("method cannot be null or empty");
        }
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }

        String id = UUID.randomUUID().toString();
        neptulon.client.Request r = new neptulon.client.Request<>(id, method, params);
        resCallbacks.put(id, cb);
        send(r);
    }

    @Override
    public void sendRequestArr(String method, ResCallback cb, Object... params) {
        sendRequest(method, params, cb);
    }

    @Override
    public void close() {
        if (!connected) {
            return;
        }

        connected = false;
        try {
            ws.close(0, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /************************************
     * WebSocketListener Implementation *
     ************************************/

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        ws = webSocket;
        connected = true;
        logger.info("Connected to server: " + ws_url);
        connCallback.connected();
    }

    @Override
    public void onFailure(IOException e, Response response) {
        connected = false;
        String reason = e.getMessage();
        logger.warning("Connection closed with error: " + reason);
        connCallback.disconnected(reason);
    }

    @Override
    public void onMessage(ResponseBody message) throws IOException {
        String msgStr = message.string();
        logger.info("Incoming message: " + msgStr);
        Message msg = gson.fromJson(msgStr, Message.class);
        if (msg.method == null || msg.method.isEmpty()) {
            // handle response message
            resCallbacks.get(msg.id).callback(new ResCtx(msg.id, msg.result, msg.error, gson));
            resCallbacks.remove(msg.id);
            return;
        }

        // handle request message
        new ReqCtx(this, msg.id, msg.method, msg.params, middleware, gson).next();
    }

    @Override
    public void onPong(Buffer payload) {
        logger.info("WebSocket pong received from server: " + ws_url);
    }

    @Override
    public void onClose(int code, String reason) {
        connected = false;
        logger.info("Connection closed to server: " + ws_url);
    }
}
