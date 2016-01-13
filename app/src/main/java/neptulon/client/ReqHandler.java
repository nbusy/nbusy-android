package neptulon.client;

import com.google.gson.Gson;

import java.util.List;

/**
 * Handler for responses.
 */
public abstract class ReqHandler<T> {
    List<Middleware> middleware;
    abstract Class<T> getType();

    abstract void handler(Response<T> res);

    void execute(Gson gson, Message msg) {
        handler(new Response<>(msg.id, gson.fromJson(msg.result, this.getType()), null));
    }
}
