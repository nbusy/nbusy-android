package com.nbusy.app.data;

import titan.client.messages.MsgMessage;

public class DataMaps {

    public static MsgMessage[] getTitanMessages(com.nbusy.app.data.Message... msgs) {
        MsgMessage[] titanMsgs = new MsgMessage[msgs.length];

        for (int i = 0; i < msgs.length; i++) {
            titanMsgs[i] = new MsgMessage(msgs[i].chatId, null, msgs[i].to, msgs[i].sent, msgs[i].body);
        }

        return titanMsgs;
    }

    public static Message[] getNBusyMessages(MsgMessage... msgs) {
        Message[] nbusyMsgs = new Message[msgs.length];

        for (int i = 0; i < msgs.length; i++) {
            nbusyMsgs[i] = Message.newIncomingMessage(msgs[i].chatId, msgs[i].from, msgs[i].time, msgs[i].message);
        }

        return nbusyMsgs;
    }

}
