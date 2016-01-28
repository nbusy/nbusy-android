package com.nbusy.sdk;

import neptulon.client.ConnImpl;
import neptulon.client.ResHandler;
import neptulon.client.Response;

/**
 * NBusy client implementation: https://github.com/nbusy/nbusy
 */
public class ClientImpl implements Client {
    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public boolean close() {
        return false;
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
