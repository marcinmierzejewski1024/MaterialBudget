package com.mierzejewski.inzynierka;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public void testName() throws Exception {


    }

    public ApplicationTest() {
        super(Application.class);
        Log.wtf("asd", "asdads");
    }
}