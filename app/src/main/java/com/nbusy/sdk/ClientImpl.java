package com.nbusy.sdk;

import neptulon.client.Conn;

/**
 * NBusy client implementation: https://github.com/nbusy/nbusy
 */
public class ClientImpl extends titan.client.ClientImpl implements Client {

    public ClientImpl(Conn conn) {
        super(conn);
    }

    public ClientImpl(String url) {
        super(url);
    }

    public ClientImpl() {
        super();
    }
}
