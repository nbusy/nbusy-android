package com.nbusy.app.data;

import java.util.ArrayList;
import java.util.List;

import titan.client.messages.MsgMessage;

/**
 * Converts objects used at different components to each other.
 * i.e. A message retrieved from NBusy server into a message format to be displayed in an activity.
 *
 * Terminology:
 * - Titan: Objects used by the Titan Java client.
 * - NBusy: Objects used in (NBusy) app's /worker and /activities namespaces.
 * - DB: Objects stored in app's database, which differ slightly from those used in activities for storage optimization.
 */
public class DataMap {

    public static MsgMessage[] nbusyToTitanMessages(com.nbusy.app.data.Message... msgs) {
        MsgMessage[] titanMsgs = new MsgMessage[msgs.length];

        for (int i = 0; i < msgs.length; i++) {
            String to = msgs[i].to;
            if (to == null || to.isEmpty()) {
                to = msgs[i].chatId;
            }
            titanMsgs[i] = new MsgMessage(msgs[i].from, to, msgs[i].sent, msgs[i].body);
        }

        return titanMsgs;
    }

    public static Message[] titanToNBusyMessages(MsgMessage... msgs) {
        Message[] nbusyMsgs = new Message[msgs.length];

        for (int i = 0; i < msgs.length; i++) {
            // hack: titan does not have chatId support yet so we use from field two times
            nbusyMsgs[i] = Message.newIncomingMessage(msgs[i].from.toLowerCase(), msgs[i].from, msgs[i].message, msgs[i].time);
        }

        return nbusyMsgs;
    }

    public static List<Message> dbToNBusyMessages(UserProfile profile, List<Message> msgs) {
        List<Message> nbMsgs = new ArrayList<>();
        Chat chat = profile.getChat(msgs.get(0).chatId).get();

        for (Message msg : msgs) {
            if (msg.owner) {
                nbMsgs.add(new Message(msg.id, msg.chatId, profile.name, chat.peerName, true, msg.body, msg.sent, msg.status));
            } else {
                nbMsgs.add(new Message(msg.id, msg.chatId, chat.peerName, profile.name, false, msg.body, msg.sent, msg.status));
            }
        }

        return nbMsgs;
    }
}
