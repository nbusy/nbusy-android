package com.nbusy.sdk.titan.neptulon;

/**
 * Handler for responses.
 */
public interface ResHandler {
    <T> void Handler(Response<T> res);
}
