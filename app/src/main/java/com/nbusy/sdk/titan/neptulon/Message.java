package com.nbusy.sdk.titan.neptulon;

/**
 * Generic JSON-RPC message type.
 */
public class Message {
    final String id;
    final String method;

    public Message(String id, String method) {
        this.id = id;
        this.method = method;
    }
}
