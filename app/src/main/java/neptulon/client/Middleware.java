package neptulon.client;

/**
 * Middleware interface definition.
 */
public interface Middleware {
    void handler(ReqCtx req);
}

