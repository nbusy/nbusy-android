package neptulon.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Response context.
 */
public class ResCtx {
    private final String id;
    private final JsonElement result; // result parameters
    private final Message.ResError error; // response error (if any)
    private final Gson gson;

    public ResCtx(String id, JsonElement result, Message.ResError error, Gson gson) {
        this.id = id;
        this.result = result;
        this.error = error;
        this.gson = gson;
    }

    public String getID() {
        return id;
    }


    public <T> T getResult(Class<T> classOfT) {
        return gson.fromJson(result, classOfT);
    }
}
