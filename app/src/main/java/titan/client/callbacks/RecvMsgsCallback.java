package titan.client.callbacks;

import titan.client.messages.Message;

public interface RecvMsgsCallback {
    void callback(Message[] msgs);
}
