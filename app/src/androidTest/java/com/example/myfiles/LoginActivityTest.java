package com.example.myfiles;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class LoginActivityTest {

    //To launch our login activity
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
    private LoginActivity mActivity = null;

    //Whenever registerAct will start, this monitor will monitor it and return the registerAct instance.
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(RegisterActivity.class.getName(),null,false);


    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunchOfRegisterOnButton(){
        mActivity.findViewById(R.id.goto_register_activity);

        onView(withId(R.id.goto_register_activity)).perform(click());
        Activity registerActivity =  getInstrumentation().waitForMonitorWithTimeout(monitor,5000);
        assertNotNull(registerActivity);
        registerActivity.finish();
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}