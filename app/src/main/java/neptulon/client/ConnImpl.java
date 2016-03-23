package neptulon.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final List<Middleware> middleware = new CopyOnWriteArrayList<>();
    private final ConcurrentMap<String, ResCallback> resCallbacks = new ConcurrentHashMap<>();
    private final String ws_url;
    private final AtomicBoolean connected = new AtomicBoolean();
    private final AtomicBoolean connecting = new AtomicBoolean();
    private WebSocket ws;
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

        final String m = gson.toJson(obj);
        logger.info("Outgoing message: " + m);
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    ws.sendMessage(RequestBody.create(WebSocket.TEXT, m));
                } catch (IOException e) {
                    e.printStackTrace();
                    close();
                }
            }
        }).start();
    }

    /***********************
     * Conn Implementation *
     ***********************/

    @Override
    public synchronized void useTLS(byte[] ca, byte[] clientCert, byte[] clientCertKey) {
        // todo: https://github.com/square/okhttp/wiki/HTTPS
        // * pin and use a single trusted custom server cert
        // * limit ciphers and TLS version
    }

    @Override
    public synchronized void setDeadline(int seconds) {

    }

    @Override
    public synchronized void middleware(Middleware mw) {
        if (mw == null) {
            throw new IllegalArgumentException("mw cannot be null");
        }

        middleware.add(mw);
    }

    // todo: add a default router since this is a client that should be functional out of box (and remote custom router from Titan client)
    // handleRequest(method, .....) { if isClientConn... else exception } // same goes for go-client

    @Override
    public synchronized void connect(ConnCallback cb) {
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        if (connecting.get()) {
            return;
        }

        // enqueue this listener implementation to initiate the WebSocket connection
        connCallback = cb;
        connecting.set(true);
        WebSocketCall.create(client, new Request.Builder().url(ws_url).build()).enqueue(this);
    }

    @Override
    public synchronized boolean isConnected() {
        return connected.get();
    }

    @Override
    public synchronized void remoteAddr() {
        if (!connected.get()) {
            throw new IllegalStateException("Not connected.");
        }
    }

    @Override
    public <T> void sendRequest(String method, T params, ResCallback cb) {
        if (!connected.get()) {
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
        if (!connected.getAndSet(false)) {
            return;
        }

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
        connecting.set(false);
        connected.set(true);
        logger.info("Connected to server: " + ws_url);
        connCallback.connected();
    }

    @Override
    public void onFailure(IOException e, Response response) {
        connected.set(false);
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
        connected.set(false);
        logger.info("Connection closed to server: " + ws_url);
    }
}
