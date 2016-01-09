package com.nbusy.sdk.titan.neptulon;

/**
 * Handler for responses.
 */
public interface ResHandler<T> {
    Class<T> Class();
    void Handler(Response<T> res);
}
