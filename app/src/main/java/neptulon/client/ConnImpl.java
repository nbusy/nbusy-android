package neptulon.client;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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
    private static final Logger logger = Logger.getLogger(ConnImpl.class.getSimpleName());
    private final Gson gson = new Gson();
    private final OkHttpClient client;
    private final Request request;
    private final WebSocketCall wsCall;
    private final List<Middleware> middleware;
    private final Map<String, ResHandler> resHandlers;
    private WebSocket ws;
    private boolean connected;

    /**
     * Initializes a new connection with given server URL.
     */
    public ConnImpl(String url) {
        middleware = new ArrayList<>();
        resHandlers = new HashMap<>();

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

    private void send(Object src) {
        try {
            ws.sendMessage(RequestBody.create(WebSocket.TEXT, gson.toJson(src)));
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    /*
     * ######## Conn Implementation ########
     */

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
        middleware.add(mw);
    }

    // todo: add default router
    // handleRequest(method, .....) { if isClientConn... else exception } // same goes for go-client

    @Override
    public void connect() {
        // add sender middleware as the last middleware in stack
        middleware.add(new Middleware() {
            @Override
            public void handler(ReqCtx req) {
                send(req);
            }
        });

        // enqueue this listener implementation to initiate the WebSocket connection
        wsCall.enqueue(this);
    }

    @Override
    public boolean isConnected() {
        return ws != null && connected;
    }

    @Override
    public void remoteAddr() {

    }

    @Override
    public <T> void sendRequest(String method, T params, ResHandler handler) {
        String id = UUID.randomUUID().toString();
        neptulon.client.Request r = new neptulon.client.Request<>(id, method, params);
        send(r);
        resHandlers.put(id, handler);
    }

    @Override
    public void sendRequestArr(String method, ResHandler handler, Object... params) {

    }

    @Override
    public void close() {
        connected = false;
        try {
            ws.close(0, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * ######## WebSocketListener Implementation ########
     */

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        ws = webSocket;
        connected = true;
        logger.info("WebSocket connected.");
    }

    @Override
    public void onFailure(IOException e, Response response) {
        connected = false;
        logger.warning("WebSocket connection closed with error: " + e.getMessage());
    }

    @Override
    public void onMessage(ResponseBody message) throws IOException {
        String msgStr = message.string();
        logger.info("Incoming message: " + msgStr);
        Message msg = gson.fromJson(msgStr, Message.class);
        if (msg.method == null || msg.method.isEmpty()) {
            // handle response message
            resHandlers.get(msg.id).execute(gson, msg);
            return;
        }

        // handle request message
        new ReqCtx(msg.id, msg.method, msg.params, middleware, gson).next();
    }

    @Override
    public void onPong(Buffer payload) {
        logger.info("WebSocket pong received.");
    }

    @Override
    public void onClose(int code, String reason) {
        connected = false;
        logger.info("WebSocket closed.");
    }
}
