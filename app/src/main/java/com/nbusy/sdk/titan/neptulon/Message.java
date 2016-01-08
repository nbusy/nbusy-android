package com.nbusy.sdk.titan.neptulon;

import com.google.gson.JsonObject;

/**
 * Outgoing JSON-RPC request object representation.
 */
class Request {
    final String id;
    final String method;
    final Object params;

    Request(String id, String method, Object params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }
}

/**
 * Outgoing JSON-RPC response object representation.
 */
class Response {
    final String id;
    final Object result;
    final ResError error;

    Response(String id, Object result, ResError error) {
        this.id = id;
        this.result = result;
        this.error = error;
    }
}

/**
 * Outgoing JSON-RPC response error object representation.
 */
class ResError {
    final int code;
    final String message;
    final Object data;

    ResError(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
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
    final JsonObject params;
    final JsonObject result;
    final ResError error;

    public Message(String id, String method, JsonObject params, JsonObject result, ResError error) {
        this.id = id;
        this.method = method;
        this.params = params;
        this.result = result;
        this.error = error;
    }
}

/**
 * Incoming JSON-RPC response error object representation.
 */
class ResErrorIn {
    final int code;
    final String message;
    final JsonObject data;

    ResErrorIn(int code, String message, JsonObject data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}

