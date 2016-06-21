package com.nbusy.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nbusy.app.data.DB;
import com.nbusy.app.data.sqldb.SQLDB;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SQLDBTest {
    @Test
    public void getDB() throws Exception {
        DB db = new SQLDB(InstrumentationRegistry.getTargetContext());
    }
}
