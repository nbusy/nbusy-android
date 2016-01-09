package com.nbusy.sdk.titan.neptulon;

import com.google.gson.JsonElement;

/**
 * JSON-RPC request object.
 */
class Request<T> {
    final String id;
    final String method;
    final T params;

    Request(String id, String method, T params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }
}

/**
 * JSON-RPC response object.
 */
class Response<T> {
    final String id;
    final T result;
    final ResError error;

    Response(String id, T result, ResError error) {
        this.id = id;
        this.result = result;
        this.error = error;
    }

    class ResError<K> {
        final int code;
        final String message;
        final K data;

        ResError(int code, String message, K data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
    }
}


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

