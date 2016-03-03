package com.nbusy.app.data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class InMemDB implements DB {
    private final List<Message> msgQueue = new ArrayList<>();

    @Override
    public void getProfile(final GetProfileCallback cb) {
        simulateDelay(new Function() {
            @Override
            public void execute() {
                ArrayList<Chat> chats = new ArrayList<>();
                chats.add(new Chat("echo", "Echo", "Yo!", new Date()));
                chats.add(new Chat(UUID.randomUUID().toString(), "Teoman Soygul", "My last message", new Date()));
                chats.add(new Chat(UUID.randomUUID().toString(), "Chuck Norris", "This is my last-first message!", new Date()));

                cb.profileRetrieved(new Profile(UUID.randomUUID().toString(), chats));
            }
        });
    }

    @Override
    public void getChatMessages(final String chatId, final GetChatMessagesCallback cb) {
        simulateDelay(new Function() {
            @Override
            public void execute() {
                cb.chatMessagesRetrieved(new LinkedList<>(
                        Arrays.asList(
                                new Message(UUID.randomUUID().toString(), chatId, "Teoman Soygul", null, true, "Lorem ip sum my message...", new Date(), Message.Status.DELIVERED_TO_USER),
                                new Message(UUID.randomUUID().toString(), chatId, "User ID: " + chatId, null, false, "Test test.", new Date(), Message.Status.DELIVERED_TO_USER))));
            }
        });
    }

    @Override
    public void saveMessages(final SaveMessagesCallback cb, final Message... msgs) {
        simulateDelay(new Function() {
            @Override
            public void execute() {
                cb.messagesSaved();
            }
        });
    }

    @Override
    public void updateMessages(final UpdateMessagesCallback cb, final Message... msgs) {
        simulateDelay(new Function() {
            @Override
            public void execute() {
                cb.messagesUpdated();
            }
        });
    }

    private void simulateDelay(final Function fn) {
        class SimulateDatabase extends AsyncTask<Object, Object, Object> {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    Thread.sleep(50);
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
