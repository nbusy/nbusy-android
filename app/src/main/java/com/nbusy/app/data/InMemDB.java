package com.nbusy.app.data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class InMemDB implements DB {

    private final Config config = new Config();

    @Override
    public synchronized void getProfile(final GetProfileCallback cb) {
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }

        simulateDelay(new Function() {
            @Override
            public void execute() {
                ArrayList<Chat> chats = new ArrayList<>();
                chats.add(new Chat("echo", "Echo", "Yo!", new Date()));
                if (config.env != Config.Env.PRODUCTION) {
                    chats.add(new Chat(UUID.randomUUID().toString(), "Teoman Soygul", "My last message", new Date()));
                    chats.add(new Chat(UUID.randomUUID().toString(), "Chuck Norris", "This is my last-first message!", new Date()));
                }

                cb.profileRetrieved(new Profile(UUID.randomUUID().toString(), chats));
            }
        });
    }

    @Override
    public synchronized void getChatMessages(final String chatId, final GetChatMessagesCallback cb) {
        if (chatId == null || chatId.isEmpty()) {
            throw new IllegalArgumentException("chatId cannot be null or empty");
        }
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }

        simulateDelay(new Function() {
            @Override
            public void execute() {
                List<Message> msgs;
                if (config.env != Config.Env.PRODUCTION) {
                    msgs = new LinkedList<>(
                            Arrays.asList(
                                    new Message(UUID.randomUUID().toString(), chatId, "Teoman Soygul", null, true, "Lorem ip sum my message...", new Date(), Message.Status.DELIVERED_TO_USER),
                                    new Message(UUID.randomUUID().toString(), chatId, "User ID: " + chatId, null, false, "Test test.", new Date(), Message.Status.DELIVERED_TO_USER)));
                } else {
                    msgs = new ArrayList<>();
                }

                cb.chatMessagesRetrieved(msgs);
            }
        });
    }

    @Override
    public synchronized void saveMessages(final SaveMessagesCallback cb, final Message... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }

        simulateDelay(new Function() {
            @Override
            public void execute() {
                cb.messagesSaved();
            }
        });
    }

    @Override
    public synchronized void updateMessages(final UpdateMessagesCallback cb, final Message... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }
        if (cb == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }

        simulateDelay(new Function() {
            @Override
            public void execute() {
                cb.messagesUpdated();
            }
        });
    }

    private void simulateDelay(final Function fn) {
        if (fn == null) {
            throw new IllegalArgumentException("callback function cannot be null");
        }

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
