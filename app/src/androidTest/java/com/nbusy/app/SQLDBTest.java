package com.nbusy.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.ImmutableSet;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.DropDBCallback;
import com.nbusy.app.data.callbacks.SeedDBCallback;
import com.nbusy.app.data.callbacks.UpsertChatsCallback;
import com.nbusy.app.data.sqldb.SQLDB;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class SQLDBTest {

    private static void awaitThrows(CountDownLatch cdl, String reason) throws InterruptedException, TimeoutException {
        if (!cdl.await(5, TimeUnit.SECONDS)) {
            throw new TimeoutException("CountDownLatch timed out after awaiting for 5 seconds with message: " + reason);
        }
    }

    private static DB getEmptyDB() throws TimeoutException, InterruptedException {
        DB db = new SQLDB(InstrumentationRegistry.getTargetContext());

        final CountDownLatch seedCounter = new CountDownLatch(1);
        db.dropDB(new DropDBCallback() {
            @Override
            public void success() {
                seedCounter.countDown();
            }

            @Override
            public void error() {
                fail("could not drop database");
            }
        });
        awaitThrows(seedCounter, "could not drop database");

        return db;
    }

    private static DB getSeededDB() throws TimeoutException, InterruptedException {
        DB db = getEmptyDB();

        final CountDownLatch seedCounter = new CountDownLatch(1);
        db.seedDB(new SeedDBCallback() {
            @Override
            public void success() {
                seedCounter.countDown();
            }

            @Override
            public void error() {
                fail("could not seed database");
            }
        });
        awaitThrows(seedCounter, "could not seed database");

        return db;
    }

    @Test
    public void getEmptyProfile() throws Exception {
        DB db = getEmptyDB();

        final CountDownLatch cbCounter = new CountDownLatch(1);
        db.getProfile(new GetProfileCallback() {
            @Override
            public void profileRetrieved(UserProfile userProfile) {
                fail("expected empty profile");
            }

            @Override
            public void error() {
                cbCounter.countDown();
            }
        });
        awaitThrows(cbCounter, "could not retrieve profile");

        final CountDownLatch cbCounter2 = new CountDownLatch(1);
        db.getProfile(new GetProfileCallback() {
            @Override
            public void profileRetrieved(UserProfile userProfile) {
                fail("expected empty profile");
            }

            @Override
            public void error() {
                cbCounter2.countDown();
            }
        });
        awaitThrows(cbCounter2, "could not retrieve profile");
    }

    @Test
    public void createThenGetProfile() throws Exception {
        DB db = getEmptyDB();

        final UserProfile profile = new UserProfile(
                "id-1234",
                "token-12jg4ec",
                "mail-chuck@nbusy.com",
                "name-chuck norris",
                new byte[]{0, 2, 3},
                new ArrayList<Chat>());

        final CountDownLatch cbCounter = new CountDownLatch(1);
        db.createProfile(profile, new CreateProfileCallback() {
            @Override
            public void success() {
                cbCounter.countDown();
            }

            @Override
            public void error() {
                fail("could not create profile");
            }
        });
        awaitThrows(cbCounter, "could not create profile");

        final CountDownLatch cbCounter2 = new CountDownLatch(1);
        db.getProfile(new GetProfileCallback() {
            @Override
            public void profileRetrieved(UserProfile up) {
                assertEquals(profile.id, up.id);
                assertEquals(profile.jwtToken, up.jwtToken);
                assertEquals(profile.email, up.email);
                assertEquals(profile.name, up.name);
                assertFalse(up.getPicture().isPresent());

                cbCounter2.countDown();
            }

            @Override
            public void error() {
                fail("expected non-null profile");
            }
        });
        awaitThrows(cbCounter2, "could not retrieve profile");
    }

    @Test
    public void createChats() throws Exception {
        DB db = getSeededDB();

        final Chat chat1 = new Chat(
                "id-chat-1234",
                "Phil Norris",
                "my last message to Phil",
                new Date(),
                ImmutableSet.<Message>of());

        final Chat chat2 = new Chat(
                "id-chat-5678",
                "Old Norris",
                "my last message to Old",
                new Date(),
                ImmutableSet.<Message>of());

        final CountDownLatch cbCounter = new CountDownLatch(1);
        db.upsertChats(new UpsertChatsCallback() {
            @Override
            public void success() {
                cbCounter.countDown();
            }

            @Override
            public void error() {
                fail("failed to persist chat(s)");
            }
        }, chat1, chat2);
        awaitThrows(cbCounter, "failed to persist chat(s)");

        final CountDownLatch cbCounter2 = new CountDownLatch(1);
        db.getProfile(new GetProfileCallback() {
            @Override
            public void profileRetrieved(UserProfile up) {
                assertEquals(2, up.getChats().size());

                assertTrue(up.getChat(chat1.id).isPresent());
                Chat dbChat1 = up.getChat(chat1.id).get();
                assertEquals(chat1.peerName, dbChat1.peerName);
                assertEquals(chat1.lastMessage, dbChat1.lastMessage);
                assertEquals(chat1.lastMessageSent, dbChat1.lastMessageSent);

                assertTrue(up.getChat(chat2.id).isPresent());
                Chat dbChat2 = up.getChat(chat2.id).get();
                assertEquals(chat2.peerName, dbChat2.peerName);
                assertEquals(chat2.lastMessage, dbChat2.lastMessage);
                assertEquals(chat2.lastMessageSent, dbChat2.lastMessageSent);

                cbCounter2.countDown();
            }

            @Override
            public void error() {
                fail("expected non-null profile");
            }
        });
        awaitThrows(cbCounter2, "failed to retrieve profile");
    }
}
