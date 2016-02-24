package titan.client;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import neptulon.client.callbacks.ConnCallback;
import titan.client.callbacks.JwtAuthCallback;
import titan.client.callbacks.RecvMsgsCallback;
import titan.client.callbacks.SendMsgCallback;
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

        Client client = new ClientImpl(WS_URL, new RecvMsgsCallback() {
            @Override
            public void callback(Message[] msgs) {
            }
        });

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
        client.sendMessages(new Message[]{new Message(null, "2", new Date(), "Hello from Titan client!")}, new SendMsgCallback() {
            @Override
            public void sentToServer() {
                System.out.println("Received 'send' response: message delivered to server.");
                msgCounter.countDown();
            }
        });
        awaitThrows(msgCounter);

        client.close();
    }
}