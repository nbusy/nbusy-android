package com.nbusy.app.data;

public class DataMaps {

    public static titan.client.messages.Message[] getTitanMessages(com.nbusy.app.data.Message[] msgs) {
        titan.client.messages.Message[] titanMsgs = new titan.client.messages.Message[msgs.length];

        for (int i = 0; i < msgs.length; i++) {
            titanMsgs[i] = new titan.client.messages.Message(null, msgs[i].to, msgs[i].sent, msgs[i].body);
        }

        return titanMsgs;
    }

}
