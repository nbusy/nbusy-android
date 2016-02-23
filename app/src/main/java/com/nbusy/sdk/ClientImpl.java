package com.nbusy.sdk;

import neptulon.client.Conn;
import titan.client.callbacks.RecvMsgCallback;

/**
 * NBusy client implementation: https://github.com/nbusy/nbusy
 */
public class ClientImpl extends titan.client.ClientImpl implements Client {

    public ClientImpl(Conn conn, RecvMsgCallback cb) {
        super(conn, cb);
    }

    public ClientImpl(String url, RecvMsgCallback cb) {
        super(url, cb);
    }
}
