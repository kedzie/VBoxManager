package com.kedzie.vbox.test;

import com.kedzie.vbox.VBoxApplication;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

public class VBoxAppTestCase extends ApplicationTestCase<VBoxApplication> {

    public VBoxAppTestCase() {
        super(VBoxApplication.class);
      }

      @SmallTest
      public void testPreconditions() {}
      
      /**
       * Test basic startup/shutdown of Application
       */
      @MediumTest
      public void testSimpleCreate() {
          createApplication(); 
      }
}

