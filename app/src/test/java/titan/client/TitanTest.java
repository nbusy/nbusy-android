package titan.client;

import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import neptulon.client.callbacks.ConnCallback;
import titan.client.callbacks.JwtAuthCallback;
import titan.client.callbacks.SendMessageCallback;
import titan.client.messages.Message;

public class TitanTest {
    private static final String URL = "ws://127.0.0.1:3001";
    private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdGVkIjoxNDU2MTQ5MjY0LCJ1c2VyaWQiOiIxIn0.wuKJ8CuDkCZYLmhgO-UlZd6v8nxKGk_PtkBwjalyjwA";

    private boolean isTravis() {
        return System.getenv().containsKey("TRAVIS");
    }

    /**
     * External client test case in line with the Titan external client test case specs and event flow.
     */
    @Test
    public void testExternalClient() throws InterruptedException {
        if (isTravis()) {
            return;
        }

        Client client = new ClientImpl(URL);

        final CountDownLatch connCounter = new CountDownLatch(1);
        client.connect(new ConnCallback() {
            @Override
            public void connected() {
                connCounter.countDown();
            }

            @Override
            public void disconnected(String reason) {
            }
        });
        connCounter.await(3, TimeUnit.SECONDS);

        final CountDownLatch authCounter = new CountDownLatch(1);
        client.jwtAuth(JWT_TOKEN, new JwtAuthCallback() {
            @Override
            public void success() {
                authCounter.countDown();
            }

            @Override
            public void fail() {
                org.junit.Assert.fail("JWT auth failed");
            }
        });
        authCounter.await(3, TimeUnit.SECONDS);

        final CountDownLatch msgCounter = new CountDownLatch(2);
        client.sendMessages(Collections.singletonList(new Message(null, "2", new Date(), "Hello from Titan client!")), new SendMessageCallback() {
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
        msgCounter.await(3, TimeUnit.SECONDS);

        client.close();
    }
}