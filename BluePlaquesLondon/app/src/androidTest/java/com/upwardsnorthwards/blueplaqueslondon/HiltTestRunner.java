package com.upwardsnorthwards.blueplaqueslondon;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

import dagger.hilt.android.testing.HiltTestApplication;

/**
 * Custom test runner for Hilt instrumented tests.
 * This enables Hilt dependency injection in androidTest classes.
 */
public class HiltTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return super.newApplication(cl, HiltTestApplication.class.getName(), context);
    }
}
