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

    public ClientImpl() {
        super();
    }

    // todo: this sdk should have proper debug/production urls to connect to and provide easy connection to dev nbusy server in docker etc.
}
