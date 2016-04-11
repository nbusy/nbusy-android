package neptulon.client;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import neptulon.client.callbacks.ConnCallback;
import neptulon.client.callbacks.ResCallback;
import neptulon.client.middleware.Echo;
import neptulon.client.middleware.Logger;
import neptulon.client.middleware.Router;
import titan.client.messages.EchoMessage;

import static neptulon.client.Utils.WS_URL;
import static neptulon.client.Utils.awaitThrows;
import static neptulon.client.Utils.isTravis;

public class NeptulonTest {
    /**
     * External client test case in line with the Neptulon external client test case specs and event flow.
     */
    @Test
    public void testExternalClient() throws InterruptedException, TimeoutException {
        if (isTravis()) {
            return;
        }

        Conn conn = new ConnImpl(WS_URL, false);
        conn.middleware(new Logger());
        Router router = new Router();
        router.request("echo", new Echo());
        conn.middleware(router);

        final CountDownLatch connCounter = new CountDownLatch(1);
        conn.connect(new ConnCallback() {
            @Override
            public void connected(String reason) {
                connCounter.countDown();
            }

            @Override
            public void disconnected(String reason) {
            }
        });
        awaitThrows(connCounter);

        final CountDownLatch msgCounter = new CountDownLatch(2);
        conn.sendRequest("echo", new EchoMessage("Hello from Java client!"), new ResCallback() {
            @Override
            public void callback(ResCtx ctx) {
                Object res = ctx.getResult(Object.class);
                System.out.println("Received 'echo' response: " + res);
                msgCounter.countDown();
            }
        });
        conn.sendRequest("close", new EchoMessage("Bye from Java client!"), new ResCallback() {
            @Override
            public void callback(ResCtx ctx) {
                Object res = ctx.getResult(Object.class);
                System.out.println("Received 'close' response: " + res);
                msgCounter.countDown();
            }
        });
        awaitThrows(msgCounter);

        conn.close();
    }
}