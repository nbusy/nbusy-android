package com.nbusy.app.data;

import java.util.Date;

public class DataMaps {
    public static titan.client.messages.Message[] getTitanMessages(com.nbusy.app.data.Message[] msgs) {
        titan.client.messages.Message[] titanMsgs = new titan.client.messages.Message[msgs.length];
        Date now = new Date();
        for (int i = 0; i < msgs.length; i++) {
            titanMsgs[i] = new titan.client.messages.Message(null, msgs[i].to, now, msgs[i].body);
        }
        return titanMsgs;
    }
}
