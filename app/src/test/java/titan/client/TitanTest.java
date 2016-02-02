package titan.client;

import neptulon.client.middleware.Echo;
import neptulon.client.middleware.Logger;
import neptulon.client.middleware.Router;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class TitanTest {
    private static final String URL = "ws://127.0.0.1:3001";

    @Test
    public void connect() throws InterruptedException {
        if (isTravis()) {
            return;
        }
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