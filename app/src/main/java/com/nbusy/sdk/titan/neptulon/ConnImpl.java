package com.nbusy.sdk.titan.neptulon;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
public class ConnImpl implements Conn {
    private final OkHttpClient client;
    private final Request request;
    private final WebSocketCall wsCall;
    private WebSocket ws;

    public ConnImpl() {
        client = new OkHttpClient.Builder()
                .connectTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        request = new Request.Builder()
                .url("ws://10.0.2.2:3010")
                .build();

        wsCall = WebSocketCall.create(client, request);
    }

    public void useTLS(byte[] ca, byte[] clientCert, byte[] clientCertKey) {
        // todo: https://github.com/square/okhttp/wiki/HTTPS
    }

    public void connect() {
        // enqueue a new listener to execute the websocket call
        wsCall.enqueue(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                ws = webSocket;
                try {
                    webSocket.sendMessage(RequestBody.create(WebSocket.TEXT, "{\"ID\": \"123\", \"Method\": \"test2\"}"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//                throw new RuntimeException("connected!");
            }

            @Override
            public void onFailure(IOException e, Response response) {
                throw new RuntimeException(e);
            }

            @Override
            public void onMessage(ResponseBody message) throws IOException {
//                private final Gson gson = new Gson();
//                Gist gist = gson.fromJson(response.body().charStream(), Gist.class);
//                for (Map.Entry<String, GistFile> entry : gist.files.entrySet()) {
//                    System.out.println(entry.getKey());
//                    System.out.println(entry.getValue().content);
//                }
                throw new RuntimeException("got message!" + message.string());
//                message.close(); needed?
            }

            @Override
            public void onPong(Buffer payload) {
                throw new RuntimeException("got pong!");
            }

            @Override
            public void onClose(int code, String reason) {
                throw new RuntimeException("closed!");
            }
            // ...
        });
    }

    public void close() throws IOException {
        wsCall.cancel();
        ws.close(0, "");
    }
}
