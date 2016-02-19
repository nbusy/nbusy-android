package titan.client;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import titan.client.callbacks.Callback;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class TitanTest {
    private static final String URL = "ws://127.0.0.1:3001";

    @Test
    public void connect() throws InterruptedException {
        if (isTravis()) {
            return;
        }

        Client client = new ClientImpl(URL);
        client.connect();
        Thread.sleep(100);
//        assertThat("Connection was not established in time.", client.isConnected());

        final CountDownLatch counter = new CountDownLatch(2); // todo: add one more for ws.onClose

        class CB implements Callback {
            @Override
            public void callback() {
                System.out.println("Received 'send' response.");
                counter.countDown();
            }
        }

        client.sendMessage("2", "Hello from Titan client!", new CB(), new CB());

        counter.await();
        client.close();
    }

    private boolean isTravis() {
        return System.getenv().containsKey("TRAVIS");
    }
}