package titan.client;

import neptulon.client.ConnImpl;
import neptulon.client.ResHandler;
import neptulon.client.Response;

/**
 * Titan client implementation: https://github.com/titan-x/titan
 */
public class ClientImpl implements Client {
    @Override
    public void connect() {

    }

    @Override
    public void close() {

    }

    private void init() {
        // TODO: remove this test code
        ConnImpl conn = new ConnImpl();
        conn.connect();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        class Test {
            final String message;

            Test(String message) {
                this.message = message;
            }
        }

        conn.sendRequest("test", new Test("wow"), new ResHandler<String>() {
            @Override
            public Class<String> getType() {
                return String.class;
            }

            @Override
            public void handler(Response<String> res) {
//                Log.i(TAG, "Received response: " + res.result);
            }
        });
    }
}
