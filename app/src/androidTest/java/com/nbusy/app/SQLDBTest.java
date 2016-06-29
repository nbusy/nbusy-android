package com.nbusy.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.DropDBCallback;
import com.nbusy.app.data.sqldb.SQLDB;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class SQLDBTest {

    private static void awaitThrows(CountDownLatch cdl) throws InterruptedException, TimeoutException {
        if (!cdl.await(5, TimeUnit.SECONDS)) {
            throw new TimeoutException("CountDownLatch timed out after awaiting for 5 seconds.");
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

            }
        });
        awaitThrows(seedCounter);

        return db;
    }

    private static DB getSeededDB() throws TimeoutException, InterruptedException {
        DB db = getEmptyDB();

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
        awaitThrows(cbCounter); // todo: add fail reason here, and derive from CB

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
        awaitThrows(cbCounter2);
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
                fail("didn't expect profile creation to fail");
            }
        });
        awaitThrows(cbCounter);

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
        awaitThrows(cbCounter2);
    }

    @Test
    public void createChats() throws Exception {
        DB db = getSeededDB();
    }
}
