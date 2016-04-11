package titan.client;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import titan.client.callbacks.JwtAuthCallback;
import titan.client.callbacks.SendMsgsCallback;
import titan.client.messages.Message;

import static neptulon.client.Utils.JWT_TOKEN;
import static neptulon.client.Utils.WS_URL;
import static neptulon.client.Utils.awaitThrows;
import static neptulon.client.Utils.isTravis;

public class TitanTest {
    /**
     * External client test case in line with the Titan external client test case specs and event flow.
     */
    @Test
    public void testExternalClient() throws Exception {
        if (isTravis()) {
            return;
        }

        Client client = new ClientImpl(WS_URL, false);

        final CountDownLatch connCounter = new CountDownLatch(1);
        client.connect(new ConnCallbacksStub() {
            @Override
            public void connected(String reason) {
                connCounter.countDown();
            }
        });
        awaitThrows(connCounter);

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
        awaitThrows(authCounter);

        final CountDownLatch msgCounter = new CountDownLatch(1);
        client.sendMessages(new SendMsgsCallback() {
            @Override
            public void sentToServer() {
                System.out.println("Received 'send' response: message delivered to server.");
                msgCounter.countDown();
            }
        }, new Message("1", null, "2", new Date(), "Hello from Titan client!"));
        awaitThrows(msgCounter);

        client.close();
    }
}