package com.nbusy.app.data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;

public class InMemDB implements DB {
    @Override
    public void getProfile(final GetProfileCallback cb) {
        simulateDelay(new Function() {
            @Override
            public void execute() {
                ArrayList<Chat> chats = new ArrayList<>();
                chats.add(new Chat("1", "Teoman Soygul", "My last message", "123456"));
                chats.add((new Chat("2", "Chuck Norris", "This is my last-first message!", "9876543")));

                cb.profileRetrieved(new Profile("1", chats));
            }
        });
    }

    @Override
    public void getChatMessages(final String chatId, final GetChatMessagesCallback cb) {
        simulateDelay(new Function() {
            @Override
            public void execute() {
                LinkedList<Message> msgs;

                if (Objects.equals(chatId, "1")) {
                    msgs = new LinkedList<>(
                            Arrays.asList(
                                    new Message(UUID.randomUUID().toString(), "1", "Teoman Soygul", null, true, "Lorem ip sum my message...", new Date(), Message.Status.DeliveredToUser),
                                    new Message(UUID.randomUUID().toString(), "1", "User ID: " + "1", null, false, "Test test.", new Date(), Message.Status.DeliveredToUser)));

                } else {
                    msgs = new LinkedList<>(
                            Arrays.asList(
                                    new Message(UUID.randomUUID().toString(), "2", "Teoman Soygul", null, true, "Lorem ip sum my message...", new Date(), Message.Status.DeliveredToUser),
                                    new Message(UUID.randomUUID().toString(), "2", "User ID: " + "2", null, false, "Test test.", new Date(), Message.Status.DeliveredToUser)));

                }

                cb.chatMessagesRetrieved(msgs);
            }
        });
    }

    private void simulateDelay(final Function fn) {
        class SimulateDatabase extends AsyncTask<Object, Object, Object> {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                fn.execute();
            }
        }
        new SimulateDatabase().execute(null, null, null);
    }

    private interface Function {
        void execute();
    }
}
