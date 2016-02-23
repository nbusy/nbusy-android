package com.nbusy.sdk;

import neptulon.client.Conn;
import titan.client.callbacks.RecvMsgsCallback;

/**
 * NBusy client implementation: https://github.com/nbusy/nbusy
 */
public class ClientImpl extends titan.client.ClientImpl implements Client {

    public ClientImpl(Conn conn, RecvMsgsCallback cb) {
        super(conn, cb);
    }

    public ClientImpl(String url, RecvMsgsCallback cb) {
        super(url, cb);
    }
}
