package com.nbusy.sdk;

import neptulon.client.Conn;

/**
 * NBusy client implementation: https://github.com/nbusy/nbusy
 */
public class ClientImpl extends titan.client.ClientImpl implements Client {

    public ClientImpl(Conn conn) {
        super(conn);
    }

    public ClientImpl(String url, boolean async) {
        super(url, async);
    }

    public ClientImpl(boolean async) {
        super("wss://nbusy.herokuapp.com", async);
    }

    public ClientImpl() {
        super("wss://nbusy.herokuapp.com", false);
    }
}
