package com.nbusy.app.data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

public class InMemoryDatabase implements Database {
    @Override
    public void getProfile(final GetProfileCallback cb) {
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
                ArrayList<Chat> chats = new ArrayList<>();
                chats.add(
                        new Chat("1", "Teoman Soygul", "My last message", "123456",
                                new LinkedList<>(
                                        Arrays.asList(
                                                new Message(UUID.randomUUID().toString(), "1", "Teoman Soygul", null, true, "Lorem ip sum my message...", new Date(), Message.Status.DeliveredToUser),
                                                new Message(UUID.randomUUID().toString(), "1", "User ID: " + "1", null, false, "Test test.", new Date(), Message.Status.DeliveredToUser)))));
                chats.add((
                        new Chat("2", "Chuck Norris", "This is my last-first message!", "9876543",
                                new LinkedList<>(
                                        Arrays.asList(
                                                new Message(UUID.randomUUID().toString(), "2", "Teoman Soygul", null, true, "Lorem ip sum my message...", new Date(), Message.Status.DeliveredToUser),
                                                new Message(UUID.randomUUID().toString(), "2", "User ID: " + "2", null, false, "Test test.", new Date(), Message.Status.DeliveredToUser))))));

                // todo: run the callback on ui thread with runOnUiThread(...)
                cb.profileRetrieved(new Profile("1", chats));
            }
        }

        new SimulateDatabase().execute(null, null, null);
    }
}
