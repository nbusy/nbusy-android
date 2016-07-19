package com.nbusy.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.ImmutableSet;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.DropDBCallback;
import com.nbusy.app.data.callbacks.SeedDBCallback;
import com.nbusy.app.data.callbacks.UpsertChatsCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;
import com.nbusy.app.data.sqldb.SQLDB;
import com.nbusy.app.data.sqldb.SeedData;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
            public void success(UserProfile userProfile) {
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
            public void success(UserProfile userProfile) {
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
            public void success(UserProfile up) {
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
    public void upsertChats() throws Exception {
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
            public void success(UserProfile up) {
                assertEquals(3, up.getChats().size());

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

    @Test
    public void crudMessages() throws Exception {
        DB db = getSeededDB();

        final Message msg1 = Message.newIncomingMessage(SeedData.chat1.id, "chuck chuck", "hey dude, what up", new Date());
        final Message msg2 = Message.newIncomingMessage(SeedData.chat1.id, "phil norris", "all good dude", new Date());
        final Message msg3 = Message.newOutgoingMessage(SeedData.chat1.id, "me me", "this is an outgoing message body");

        // save some messages
        final CountDownLatch cbCounter = new CountDownLatch(1);
        db.upsertMessages(new UpsertMessagesCallback() {
            @Override
            public void success() {
                cbCounter.countDown();
            }

            @Override
            public void error() {
                fail("failed to persist message(s)");
            }
        }, msg1, msg2, msg3);
        awaitThrows(cbCounter, "failed to persist message(s)");

        // get newly saved messages
        final CountDownLatch cbCounter2 = new CountDownLatch(1);
        db.getChatMessages(SeedData.chat1.id, new GetChatMessagesCallback() {
            @Override
            public void chatMessagesRetrieved(final List<Message> msgs) {
                assertEquals(5, msgs.size());

                Message dbMsg1 = msgs.get(2);
                assertEquals(msg1.id, dbMsg1.id);
                assertEquals(msg1.chatId, dbMsg1.chatId);
                assertEquals(msg1.from, dbMsg1.from);
                assertEquals(msg1.body, dbMsg1.body);
                assertEquals(msg1.sent, dbMsg1.sent);
                assertEquals(msg1.status, dbMsg1.status);

                Message dbMsg2 = msgs.get(3);
                assertEquals(msg2.id, dbMsg2.id);
                assertEquals(msg2.chatId, dbMsg2.chatId);
                assertEquals(msg2.from, dbMsg2.from);
                assertEquals(msg2.body, dbMsg2.body);
                assertEquals(msg2.sent, dbMsg2.sent);
                assertEquals(msg2.status, dbMsg2.status);

                cbCounter2.countDown();
            }
        });
        awaitThrows(cbCounter2, "failed to retrieve messages");

        // get unsent (queued) messages
        final CountDownLatch cbCounter3 = new CountDownLatch(1);
        db.getQueuedMessages(new GetChatMessagesCallback() {
            @Override
            public void chatMessagesRetrieved(final List<Message> msgs) {
                assertEquals(2, msgs.size());
                cbCounter3.countDown();
            }
        });
        awaitThrows(cbCounter3, "failed to retrieve messages");
    }

    @Test
    public void conflictResolution() throws Exception {
        DB db = getSeededDB();
        final Message msg2 = SeedData.msg2;
        final Message msg2mod = new Message(msg2.id, msg2.chatId, msg2.from, msg2.to, msg2.owner, "modified body!", msg2.sent, msg2.status);

        // save an existing message again
        final CountDownLatch cbCounter = new CountDownLatch(1);
        db.upsertMessages(new UpsertMessagesCallback() {
            @Override
            public void success() {
                cbCounter.countDown();
            }

            @Override
            public void error() {
                fail("failed to persist message(s)");
            }
        }, msg2mod);
        awaitThrows(cbCounter, "failed to persist message(s)");

        // get newly saved messages
        final CountDownLatch cbCounter2 = new CountDownLatch(1);
        db.getChatMessages(SeedData.chat1.id, new GetChatMessagesCallback() {
            @Override
            public void chatMessagesRetrieved(final List<Message> msgs) {
                assertEquals(2, msgs.size());
                Message dbMsg2 = msgs.get(1);
                assertEquals(msg2.id, dbMsg2.id);
                assertEquals("modified body!", dbMsg2.body);
                cbCounter2.countDown();
            }
        });
        awaitThrows(cbCounter2, "failed to retrieve messages");
    }
}
