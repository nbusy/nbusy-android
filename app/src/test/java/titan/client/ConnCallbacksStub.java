package titan.client;

import titan.client.callbacks.ConnCallbacks;
import titan.client.messages.Message;

public class ConnCallbacksStub implements ConnCallbacks {
    @Override
    public void messagesReceived(Message[] msgs) {

    }

    @Override
    public void connected() {

    }

    @Override
    public void disconnected(String reason) {

    }
}
