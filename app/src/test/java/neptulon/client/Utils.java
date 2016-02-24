package neptulon.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Utils {
    public static final String WS_URL = "ws://127.0.0.1:3001";
    public static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdGVkIjoxNDU2MTQ5MjY0LCJ1c2VyaWQiOiIxIn0.wuKJ8CuDkCZYLmhgO-UlZd6v8nxKGk_PtkBwjalyjwA";

    public static boolean isTravis() {
        return System.getenv().containsKey("TRAVIS");
    }

    public static void awaitThrows(CountDownLatch cdl) throws InterruptedException, TimeoutException {
        if (!cdl.await(5, TimeUnit.SECONDS)) {
            throw new TimeoutException("CountDownLatch timed out after awaiting for 5 seconds.");
        }
    }
}
