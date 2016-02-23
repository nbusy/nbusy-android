package neptulon.client;

/**
 * Middleware interface definition.
 */
public interface Middleware {

    /**
     * Middleware interface signature.
     * @param ctx Request context.
     */
    void middleware(ReqCtx ctx);
}
