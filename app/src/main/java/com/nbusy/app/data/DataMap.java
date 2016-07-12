package com.nbusy.app.data;

import java.util.ArrayList;
import java.util.List;

import titan.client.messages.MsgMessage;

public class DataMap {

    public static MsgMessage[] nbusyToTitanMessages(com.nbusy.app.data.Message... msgs) {
        MsgMessage[] titanMsgs = new MsgMessage[msgs.length];

        for (int i = 0; i < msgs.length; i++) {
            titanMsgs[i] = new MsgMessage(msgs[i].chatId, null, msgs[i].to, msgs[i].sent, msgs[i].body);
        }

        return titanMsgs;
    }

    public static Message[] titanToNBusyMessages(MsgMessage... msgs) {
        Message[] nbusyMsgs = new Message[msgs.length];

        for (int i = 0; i < msgs.length; i++) {
            nbusyMsgs[i] = Message.newIncomingMessage(msgs[i].chatId, msgs[i].from, msgs[i].message, msgs[i].time);
        }

        return nbusyMsgs;
    }

    public static List<Message> dbToNBusyMessages(UserProfile profile, List<Message> msgs) {
        List<Message> convertedMsgs = new ArrayList<>();
        for (Message msg : msgs) {
            if (msg.owner) {
//                msg.from = profile.name;
            }
        }

        return msgs;
    }
}
