package com.example.projectpixer;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static org.junit.Assert.*;

public class FaceDetectionFunctionTest {

    @Rule
    public ActivityTestRule<FaceDetectionFunction> objectFunctionActivityTestRule = new ActivityTestRule<FaceDetectionFunction>(FaceDetectionFunction.class);

    private FaceDetectionFunction mObject = null;

    @Before
    public void setUp() throws Exception {

        View imgView = mObject.findViewById(R.id.imageView);
        View snapBtn = mObject.findViewById(R.id.snapBtn);
        View selectBtn = mObject.findViewById(R.id.selectBtn);
        View textView = mObject.findViewById(R.id.process);
        assertNotNull(snapBtn);
        assertNotNull(selectBtn);
        assertNotNull(imgView);
        assertNotNull(textView);

    }

    @After
    public void tearDown() throws Exception {

        mObject = null;

    }
}