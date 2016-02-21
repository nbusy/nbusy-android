package titan.client;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import neptulon.client.ConnHandler;
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
        final CountDownLatch connCounter = new CountDownLatch(1);
        final CountDownLatch msgCounter = new CountDownLatch(2);
        client.connect(new ConnHandler() {
            @Override
            public void connected() {
                connCounter.countDown();
            }

            @Override
            public void disconnected(String reason) {

            }
        });
        connCounter.await(1, TimeUnit.SECONDS);

        class CB implements Callback {
            @Override
            public void callback() {
                System.out.println("Received 'send' response.");
                msgCounter.countDown();
            }
        }

        client.sendMessage("2", "Hello from Titan client!", new CB(), new CB());

        msgCounter.await();
        client.close();
    }

    private boolean isTravis() {
        return System.getenv().containsKey("TRAVIS");
    }
}