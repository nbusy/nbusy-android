package com.nbusy.sdk;

/**
 * NBusy client interface: https://github.com/nbusy/nbusy
 */
public interface Client {
    /**
     *
     * @return
     */
    boolean connect();

    /**
     *
     * @return
     */
    boolean close();

//    ListenableFuture<String> connectAsync(); // this could go into NBusyAsync // still, future vs callback?
}
