package titan.client;

import titan.client.callbacks.ConnCallbacks;
import titan.client.messages.MsgMessage;

public class ConnCallbacksStub implements ConnCallbacks {
    @Override
    public void messagesReceived(MsgMessage[] msgs) {

    }

    @Override
    public void connected(String reason) {

    }

    @Override
    public void disconnected(String reason) {

    }
}
