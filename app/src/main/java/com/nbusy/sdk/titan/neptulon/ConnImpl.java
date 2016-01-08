package com.nbusy.sdk.titan.neptulon;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private WebSocket ws;

    /**
     * Initializes a new connection with given server URL.
     */
    public ConnImpl(String url) {
        middleware = new ArrayList<>();

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
        this("ws://10.0.2.2:3010");
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

    @Override
    public void connect() {
        // enqueue this listener implementation to initiate the WebSocket connection
        wsCall.enqueue(this);
    }

    @Override
    public void remoteAddr() {

    }

    @Override
    public void sendRequest(int resHandler) {
        Object src = new Object();
        try {
            ws.sendMessage(RequestBody.create(WebSocket.TEXT, gson.toJson(src)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRequestArr(int resHandler) {

    }

    @Override
    public void close() {
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
        logger.info("WebSocket connected.");
    }

    @Override
    public void onFailure(IOException e, Response response) {
        logger.warning("WebSocket connection closed with error: " + e.getMessage());
    }

    @Override
    public void onMessage(ResponseBody message) throws IOException {
        Message msg = gson.fromJson(message.string(), Message.class);
        if (msg.method.isEmpty()) {
            // handle response message
            // todo: return resHandler(ResCtx);
            return;
        }

        // handle request message
        // todo: return new ReqCtx(....).Next();
    }

    @Override
    public void onPong(Buffer payload) {
        logger.info("WebSocket pong received.");
    }

    @Override
    public void onClose(int code, String reason) {
        logger.info("WebSocket closed.");
    }
}
