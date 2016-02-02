package com.nbusy.sdk;

/**
 * NBusy client implementation: https://github.com/nbusy/nbusy
 */
public class ClientImpl extends titan.client.ClientImpl implements Client {

    public ClientImpl() {
        super(conn);
    }
}
