package titan.client;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import neptulon.client.callbacks.ConnCallback;
import titan.client.callbacks.SendMessageCallback;

public class TitanTest {
    private static final String URL = "ws://127.0.0.1:3001";
    private boolean isTravis() {
        return System.getenv().containsKey("TRAVIS");
    }

    @Test
    public void neptulonTestExternalClient() throws InterruptedException {
        if (isTravis()) {
            return;
        }

        Client client = new ClientImpl(URL);
        final CountDownLatch connCounter = new CountDownLatch(1);
        final CountDownLatch msgCounter = new CountDownLatch(2);
        client.connect(new ConnCallback() {
            @Override
            public void connected() {
                connCounter.countDown();
            }

            @Override
            public void disconnected(String reason) {

            }
        });
        connCounter.await(1, TimeUnit.SECONDS);

        client.sendMessage("2", "Hello from Titan client!", new SendMessageCallback() {
            @Override
            public void sentToServer() {
                System.out.println("Received 'send' response: message delivered to server.");
                msgCounter.countDown();
            }

            @Override
            public void delivered() {
                System.out.println("Received 'send' response: message delivered to user.");
                msgCounter.countDown();
            }
        });

        msgCounter.await();
        client.close();
    }
}