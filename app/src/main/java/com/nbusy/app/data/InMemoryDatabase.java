package com.nbusy.app.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class InMemoryDatabase implements Database {
    @Override
    public void getProfile(GetProfileCallback cb) {
        ArrayList<Chat> chats = new ArrayList<Chat>();
        chats.add(new Chat("1", "Teoman Soygul", "My last message", "123456", Arrays.asList(new Message(UUID.randomUUID().toString(), "1", "Teoman Soygul", null, true, "Lorem ip sum my message...", new Date(), Message.Status.DeliveredToUser),
                new Message(UUID.randomUUID().toString(), "1", "User ID: " + "1", null, false, "Test test.", new Date(), Message.Status.DeliveredToUser))));
        chats.add((new Chat("2", "Chuck Norris", "This is my last-first message!", "9876543", Arrays.asList(new Message(UUID.randomUUID().toString(), "2", "Teoman Soygul", null, true, "Lorem ip sum my message...", new Date(), Message.Status.DeliveredToUser),
                new Message(UUID.randomUUID().toString(), "2", "User ID: " + "2", null, false, "Test test.", new Date(), Message.Status.DeliveredToUser)))));
        cb.profileRetrieved(new Profile("1", chats));
    }
}
