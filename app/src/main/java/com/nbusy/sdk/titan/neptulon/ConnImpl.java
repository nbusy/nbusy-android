package com.nbusy.sdk.titan.neptulon;

import java.io.IOException;
import java.util.Map;

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
    public ConnImpl() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .writeTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
                .url("ws://10.0.2.2:3010")
                .build();
        WebSocketCall call = WebSocketCall.create(client, request);
        call.enqueue(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
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
//        call.cancel();
    }
}
