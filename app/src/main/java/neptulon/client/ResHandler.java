package neptulon.client;

import com.google.gson.Gson;

// todo: make this into an interface and move to callbacks package:
// interface ResCallback { callback(ResCtx ctx); }

/**
 * Handler for responses.
 */
public abstract class ResHandler<T> {
    public abstract Class<T> getType();

    public abstract void handler(Response<T> res);

    public void execute(Gson gson, Message msg) {
        handler(new Response<>(msg.id, gson.fromJson(msg.result, this.getType()), null));
    }
}
