package neptulon.client;

import com.google.gson.JsonElement;


/**
 * Generic (request or response) JSON-RPC message representation for incoming messages.
 * Initially we don't know the received message type so rely on a generic type that contains everything.
 * If Method field is not empty, this is a request message, otherwise a response.
 */
class Message {
    final String id;
    final String method;
    final JsonElement params;
    final JsonElement result;
    final ResError error;

    public Message(String id, String method, JsonElement params, JsonElement result, ResError error) {
        this.id = id;
        this.method = method;
        this.params = params;
        this.result = result;
        this.error = error;
    }

    class ResError {
        final int code;
        final String message;
        final JsonElement data;

        ResError(int code, String message, JsonElement data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
    }
}

