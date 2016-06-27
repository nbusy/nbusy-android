package com.nbusy.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.SeedDBCallback;
import com.nbusy.app.data.sqldb.SQLDB;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class SQLDBTest {

    private DB db;

    public static void awaitThrows(CountDownLatch cdl) throws InterruptedException, TimeoutException {
        if (!cdl.await(5, TimeUnit.SECONDS)) {
            throw new TimeoutException("CountDownLatch timed out after awaiting for 5 seconds.");
        }
    }

    @Before
    public void setUp() throws Exception {
        db = new SQLDB(InstrumentationRegistry.getTargetContext());

        final CountDownLatch seedCounter = new CountDownLatch(1);
        db.seedDB(new SeedDBCallback() {
            @Override
            public void success() {
                seedCounter.countDown();
            }
        });
        awaitThrows(seedCounter);
    }

    @Test
    public void getEmptyProfile() throws Exception {
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
        awaitThrows(cbCounter);

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
        final UserProfile profile = new UserProfile(
                "1234",
                "sadfsdgfgafdg",
                "chuck@nbusy.com",
                "chuck norris",
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

                cbCounter2.countDown();
            }

            @Override
            public void error() {
                cbCounter2.countDown();
                fail("expected non-null profile");
            }
        });
        awaitThrows(cbCounter2);
    }
}
