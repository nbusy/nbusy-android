package titan.client.callbacks;

import neptulon.client.callbacks.ConnCallback;
import titan.client.messages.MsgMessage;

public interface ConnCallbacks extends ConnCallback {
    void messagesReceived(MsgMessage... msgs);
}
