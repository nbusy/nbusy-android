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

    public final boolean isSuccess;
    public final int errorCode;
    public final String errorMessage;

    public ResCtx(String id, JsonElement result, Message.ResError error, Gson gson) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (gson == null) {
            throw new IllegalArgumentException("gson cannot be null");
        }

        this.id = id;
        this.result = result;
        this.error = error;
        this.gson = gson;
        this.isSuccess = (error == null);
        if (!this.isSuccess) {
            this.errorCode = this.error.code;
            this.errorMessage = this.error.message;
        } else {
            this.errorCode = 0;
            this.errorMessage = null;
        }
    }

    public String getID() {
        return id;
    }

    public <T> T getResult(Class<T> classOfT) {
        if (!isSuccess) {
            throw new IllegalStateException("Cannot read result data since server returned an error.");
        }
        if (result == null) {
            throw new IllegalStateException("Server did not return any response data.");
        }

        return gson.fromJson(result, classOfT);
    }

    public <T> T getErrorData(Class<T> classOfT) {
        if (isSuccess) {
            throw new IllegalStateException("Cannot read error data since server returned a success response.");
        }
        if (error.data == null) {
            throw new IllegalStateException("Server did not return any error data.");
        }

        return gson.fromJson(error.data, classOfT);
    }
}
