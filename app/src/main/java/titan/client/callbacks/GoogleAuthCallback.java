package titan.client.callbacks;

import titan.client.responses.GoogleAuthResponse;

public interface GoogleAuthCallback {
    void success(GoogleAuthResponse res);
    void fail();
}
