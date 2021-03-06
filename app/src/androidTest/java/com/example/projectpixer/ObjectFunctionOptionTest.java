package com.example.projectpixer;


import android.view.View;

import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ObjectFunctionOptionTest {


    @Rule
    public ActivityTestRule<ObjectFunctionOption> objectFunctionActivityTestRule = new ActivityTestRule<ObjectFunctionOption>(ObjectFunctionOption.class);

    private ObjectFunctionOption mObject = null;

    @Before
    public void setUp() throws Exception {

        mObject = objectFunctionActivityTestRule.getActivity();

    }

    @Test
    public void testLaunch() throws Exception {

        View scanBtn = mObject.findViewById(R.id.scanBtn);
        View repotBtn = mObject.findViewById(R.id.reportBtn);
        assertNotNull(scanBtn);
        assertNotNull(repotBtn);
    }


//    @Test
//    public void testLaunch(){
//
//
//
//    }

//    @Test
//    public void navigatorTest(){
//
//        TestNavHostController navController = new TestNavHostController(
//                ApplicationProvider.getApplicationContext());
//        navController.setGraph(R.navigation.triva);
//
//    }

    @After
    public void tearDown() throws Exception {

        mObject = null;

    }
}