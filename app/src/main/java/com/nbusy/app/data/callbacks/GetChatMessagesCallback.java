package com.nbusy.app.data.callbacks;

import com.nbusy.app.data.Message;

import java.util.List;

public interface GetChatMessagesCallback {
    void chatMessagesRetrieved(List<Message> msgs);
}
