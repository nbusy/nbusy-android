package titan.client.callbacks;

import neptulon.client.callbacks.ConnCallback;
import titan.client.messages.Message;

public interface ConnCallbacks extends ConnCallback {
    void messagesReceived(Message[] msgs);
}
