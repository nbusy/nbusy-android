package neptulon.client;

import java.util.Timer;
import java.util.logging.Logger;

import neptulon.client.callbacks.ConnCallback;

// todo: needs review

/**
 * This class extends {ConnImpl} with auto-reconnect functionality.
 */
public class ReconnImpl extends ConnImpl {
    private static final Logger logger = Logger.getLogger("Titan: " + ReconnImpl.class.getSimpleName());
    private boolean firstConnection = true;
    private boolean manuallyDisconnected = false;
    private boolean retriesExhausted = false;
    private final Timer timer = new Timer();
    private final int retryLimit = 7;
    private final int retryDelay = 5; // seconds
    private int retryCount = 0;

    @Override
    public synchronized void connect(final ConnCallback cb) {
        super.connect(new ConnCallback() {
            @Override
            public void connected() {
                // only fire connected event once
                if (firstConnection) {
                    cb.connected();
                }
                firstConnection = false;
            }

            @Override
            public void disconnected(String reason) {
                // only fire disconnected event when retries are exhausted or on manual disconnect call
                if (manuallyDisconnected || retriesExhausted) {
                    cb.disconnected(reason);
                    return;
                }

                // try to reconnect
                if (retryCount <= retryLimit) {
                    connect(this);
                }

                retryCount++;

                // todo: do this in a background thread with exponential backoff
//                    timer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            // Your database code here
//                        }
//                    }, 2*60*1000);
            }
        });
    }

    @Override
    public synchronized void close() {
        manuallyDisconnected = true;
        super.close();
    }
}
