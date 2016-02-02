package neptulon.client;

import neptulon.client.middleware.Echo;
import neptulon.client.middleware.Logger;
import neptulon.client.middleware.Router;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

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

        conn.connect();
        Thread.sleep(100);
        assertThat("Connection was not established in time.", conn.isConnected());

        final CountDownLatch counter = new CountDownLatch(2); // todo: add one more for ws.onClose

        conn.sendRequest("echo", new EchoMessage("Hello from Java client!"), new ResHandler<Object>() {
            @Override
            public Class<Object> getType() {
                return Object.class;
            }

            @Override
            public void handler(Response<Object> res) {
                System.out.println("Received 'echo' response: " + res.result);
                counter.countDown();
            }
        });

        conn.sendRequest("close", new EchoMessage("Bye from Java client!"), new ResHandler<Object>() {
            @Override
            public Class<Object> getType() {
                return Object.class;
            }

            @Override
            public void handler(Response<Object> res) {
                System.out.println("Received 'close' response: " + res.result);
                counter.countDown();
            }
        });

        counter.await();
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