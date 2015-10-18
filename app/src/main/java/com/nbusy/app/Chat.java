package com.nbusy.app;

final class Chat {
    public final String name; // peer name
    public final String lastMessage; // last message in conversation
    public final String sent; // last message sent date/time

    public Chat(String name, String lastMessage, String sent) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.sent = sent;
    }
}
