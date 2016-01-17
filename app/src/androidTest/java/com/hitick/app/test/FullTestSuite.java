package com.hitick.app.test;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;

/**
 * Created by Sparsha on 11/11/2015.
 */
public class FullTestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class).
                includeAllPackagesUnderHere().build();
    }

    public FullTestSuite(){
        super();
    }
}
