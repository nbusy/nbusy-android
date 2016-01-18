package neptulon.client;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class NeptulonTest {
    public static final String URL = "ws://127.0.0.1:3000";

    @Test
    public void emailValidator_CorrectEmailSimple_ReturnsTrue() {
        assertThat("hazelnuts", 3, equalTo(3));
    }

    @Test
    public void connect() {
        if (isTravis()) {
            return;
        }

        class Test {
            final String message;

            Test(String message) {
                this.message = message;
            }
        }

        Conn conn = new ConnImpl(URL);
        conn.connect();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat("Connection was not established in time.", conn.isConnected());

        // todo: add middleware to log the incoming message here and replace logger inside response handler with verifier
        // and release v0.1 corresponding to Neptulon v0.1

        conn.middleware(new Middleware() {
            @Override
            public void handler(ReqCtx req) {

            }
        });

        conn.sendRequest("test", new Test("wow"), new ResHandler<String>() {
            @Override
            public Class<String> getType() {
                return String.class;
            }

            @Override
            public void handler(Response<String> res) {
                System.out.println("Received response: " + res.result);
            }
        });
    }

    private boolean isTravis() {
        return System.getenv().containsKey("TRAVIS");
    }
}
