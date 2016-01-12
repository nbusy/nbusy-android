package neptulon;

/**
 * Middleware interface definition.
 */
public interface Middleware {
    void handler(ReqHandler req);
}

