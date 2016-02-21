package neptulon.client;

import neptulon.client.callbacks.ConnCallback;
import neptulon.client.callbacks.ResCallback;
import neptulon.client.middleware.Echo;
import neptulon.client.middleware.Logger;
import neptulon.client.middleware.Router;

import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;

public class NeptulonTest {
    private static final String URL = "ws://127.0.0.1:3001";

    @Test
    public void connect() throws InterruptedException {
        if (isTravis()) {
            return;
        }

        Conn conn = new ConnImpl(URL);
        conn.middleware(new Logger());
        Router router = new Router();
        router.request("echo", new Echo());
        conn.middleware(router);
        final CountDownLatch connCounter = new CountDownLatch(1);
        final CountDownLatch msgCounter = new CountDownLatch(2);

        conn.connect(new ConnCallback() {
            @Override
            public void connected() {
                connCounter.countDown();
            }

            @Override
            public void disconnected(String reason) {
            }
        });
        connCounter.await(1, TimeUnit.SECONDS);

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

        msgCounter.await();
        conn.close();
    }

    private boolean isTravis() {
        return System.getenv().containsKey("TRAVIS");
    }

    private class EchoMessage {
        final String message;

        EchoMessage(String message) {
            this.message = message;
        }
    }
}