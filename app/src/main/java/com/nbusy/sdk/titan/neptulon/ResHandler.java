package com.nbusy.sdk.titan.neptulon;

/**
 * Handler for responses.
 */
public interface ResHandler<T> {
    Class<T> getType();

    void Handler(Response<T> res);
}
