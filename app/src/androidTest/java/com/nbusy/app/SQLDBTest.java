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

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.fail;

@RunWith(AndroidJUnit4.class)
public class SQLDBTest {
    public static void awaitThrows(CountDownLatch cdl) throws InterruptedException, TimeoutException {
        if (!cdl.await(5, TimeUnit.SECONDS)) {
            throw new TimeoutException("CountDownLatch timed out after awaiting for 5 seconds.");
        }
    }

    // todo: set env to test (or in test script as env var?)

    @Test
    public void getEmptyProfile() throws Exception {
        DB db = new SQLDB(InstrumentationRegistry.getTargetContext());

        final CountDownLatch seedCounter = new CountDownLatch(1);
        db.seedDB(new SeedDBCallback() {
            @Override
            public void success() {
                seedCounter.countDown();
            }
        });
        awaitThrows(seedCounter);

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

        // todo: call getProfile again and verify that it still errors
    }

    @Test
    public void createThenGetProfile() throws TimeoutException, InterruptedException {
        DB db = new SQLDB(InstrumentationRegistry.getTargetContext());
        UserProfile profile = new UserProfile(
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
//        awaitThrows(cbCounter);
    }
}
