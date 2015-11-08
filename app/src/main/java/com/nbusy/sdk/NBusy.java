package com.nbusy.sdk;

/**
 * NBusy client interface: https://github.com/nbusy/nbusy
 */
public interface NBusy {
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

//    ListenableFuture<String> connectAsync();
}
