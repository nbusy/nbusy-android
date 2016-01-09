package com.nbusy.sdk.titan.neptulon;

import com.google.gson.Gson;

/**
 * Handler for responses.
 */
public abstract class ResHandler<T> {
    abstract Class<T> getType();

    abstract void handler(Response<T> res);

    void execute(Gson gson, Message msg) {
        handler(new Response<>(msg.id, gson.fromJson(msg.result, this.getType()), null));
    }
}
