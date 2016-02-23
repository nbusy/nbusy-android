package neptulon.client;

import neptulon.client.callbacks.ConnCallback;
import neptulon.client.callbacks.ResCallback;
import neptulon.client.middleware.Echo;
import neptulon.client.middleware.Logger;
import neptulon.client.middleware.Router;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NeptulonTest {
    private static final String URL = "ws://127.0.0.1:3001";

    private boolean isTravis() {
        return System.getenv().containsKey("TRAVIS");
    }

    private class EchoMessage {
        final String message;

        EchoMessage(String message) {
            this.message = message;
        }
    }

    /**
     * External client test case in line with the Neptulon external client test case specs and event flow.
     */
    @Test
    public void testExternalClient() throws InterruptedException {
        if (isTravis()) {
            return;
        }

        Conn conn = new ConnImpl(URL);
        conn.middleware(new Logger());
        Router router = new Router();
        router.request("echo", new Echo());
        conn.middleware(router);

        final CountDownLatch connCounter = new CountDownLatch(1);
        conn.connect(new ConnCallback() {
            @Override
            public void connected() {
                connCounter.countDown();
            }

            @Override
            public void disconnected(String reason) {
            }
        });
        connCounter.await(3, TimeUnit.SECONDS);

        final CountDownLatch msgCounter = new CountDownLatch(2);
        conn.sendRequest("echo", new EchoMessage("Hello from Java client!"), new ResCallback() {
            @Override
            public void handleResponse(ResCtx ctx) {
                Object res = ctx.getResult(Object.class);
                System.out.println("Received 'echo' response: " + res);
                msgCounter.countDown();
            }
        });
        conn.sendRequest("close", new EchoMessage("Bye from Java client!"), new ResCallback() {
            @Override
            public void handleResponse(ResCtx ctx) {
                Object res = ctx.getResult(Object.class);
                System.out.println("Received 'close' response: " + res);
                msgCounter.countDown();
            }
        });
        msgCounter.await(3, TimeUnit.SECONDS);

        conn.close();
    }
}