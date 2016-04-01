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
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import neptulon.client.callbacks.ConnCallback;
import neptulon.client.callbacks.ResCallback;
import neptulon.client.middleware.Router;
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
    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final Router router = new Router();
    private WebSocketCall wsConnectRequest;
    private WebSocket ws;
    private ConnCallback connCallback;

    private boolean firstConnection = true;
    private final int retryLimit = 7;
    private int retryDelay = 5; // seconds (x2 backoff for each retry)
    private int retryCount = 0;

    public enum State {
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        CLOSED
    }

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

        this.middleware(new neptulon.client.middleware.Logger());
        this.middleware(router);
    }

    /**
     * Initializes a new connection with default server URL: "ws://10.0.2.2:3000"
     * which connects to "ws://127.0.0.1:3000" on Android emulator host machine.
     */
    public ConnImpl() {
        this("ws://10.0.2.2:3000");
    }

    private void reconnect(String reason) {
        State s = state.get();
        if (s == State.CONNECTED || s == State.CONNECTING) {
            return;
        }
        if (s == State.CLOSED || retryCount >= retryLimit) {
            if (retryCount >= retryLimit) {
                reason += " and retry limits reacted";
            }
            connCallback.disconnected(reason);
            retryCount = 0;
            return;
        }

        // try to reconnect
        retryCount++;
        connect(connCallback);

        // todo: do this in a background thread with exponential backoff, though for this, we need a 3rd state called 'reconnecting'
//                    timer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            // Your database code here
//                        }
//                    }, 2*60*1000);
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

    @Override
    public synchronized void handleRequest(String route, Middleware mw) {
        router.request(route, mw);
    }

    @Override
    public synchronized void connect(ConnCallback cb) {
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        if (isConnected()) {
            return;
        }

        // enqueue this listener implementation to initiate the WebSocket connection
        connCallback = cb;
        state.set(State.CONNECTING);
        wsConnectRequest = WebSocketCall.create(client, new Request.Builder().url(ws_url).build());
        wsConnectRequest.enqueue(this);
    }

    @Override
    public boolean isConnected() {
        return state.get() == State.CONNECTED;
    }

    @Override
    public <T> void sendRequest(String method, T params, ResCallback cb) {
        if (method == null || method.isEmpty()) {
            throw new IllegalArgumentException("method cannot be null or empty");
        }
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        if (!isConnected()) {
            throw new IllegalStateException("Not connected.");
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
    public synchronized void close() {
        State s = state.getAndSet(State.CLOSED);

        // if connecting, cancel that
        if (s == State.CONNECTING) {
            wsConnectRequest.cancel();
        }

        try {
            ws.close(0, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /************************************
     * WebSocketListener Implementation *
     ************************************/

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        ws = webSocket;
        state.set(State.CONNECTED);
        logger.info("Connected to server: " + ws_url);

        // only fire connected event once and not for reconnects
        if (firstConnection) {
            connCallback.connected();
        }
        firstConnection = false;
    }

    @Override
    public void onFailure(IOException e, Response response) {
        state.set(State.DISCONNECTED);
        String reason = e.getMessage();
        logger.info("Connection attempt failed, server: " + ws_url + ", reason: " + reason);
        reconnect(reason);
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
        // if the user didn't manually close the connection, then server sent a close message
        if (state.get() != State.CLOSED) {
            state.set(State.DISCONNECTED);
        }

        logger.info("Connection closed to server: " + ws_url + ", with reason: " + reason);
        reconnect(reason);
    }
}
