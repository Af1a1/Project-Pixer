package com.example.projectpixer;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class expressionFunctionOptionTest {

    @Rule
    public ActivityTestRule<expressionFunctionOption> exptFunctionActivityTestRule = new ActivityTestRule<expressionFunctionOption>(expressionFunctionOption.class);

    private expressionFunctionOption mObject = null;

    @Before
    public void setUp() throws Exception {

        mObject = exptFunctionActivityTestRule.getActivity();

    }

    @Test
    public void testLaunch() throws Exception {

        View scanBtn = mObject.findViewById(R.id.scanBtn);
        View repotBtn = mObject.findViewById(R.id.reportBtn);
        assertNotNull(scanBtn);
        assertNotNull(repotBtn);
    }

    @After
    public void tearDown() throws Exception {

        mObject = null;

    }
}