package com.kedzie.vbox.test.activity;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.server.HelpActivity;

/**
 * An example of an {@link ActivityInstrumentationTestCase} of a specific activity {@link Focus2}.
 * By virtue of extending {@link ActivityInstrumentationTestCase}, the target activity is automatically
 * launched and finished before and after each test.  This also extends
 * {@link android.test.InstrumentationTestCase}, which provides
 * access to methods for sending events to the target activity, such as key and
 * touch events.  See {@link #sendKeys}.
 *
 * In general, {@link android.test.InstrumentationTestCase}s and {@link ActivityInstrumentationTestCase}s
 * are heavier weight functional tests available for end to end testing of your
 * user interface.  When run via a {@link android.test.InstrumentationTestRunner},
 * the necessary {@link android.app.Instrumentation} will be injected for you to
 * user via {@link #getInstrumentation} in your tests.
 *
 * See {@link com.example.android.apis.app.ForwardingTest} for an example of an Activity unit test.
 *
 * See {@link com.example.android.apis.AllTests} for documentation on running
 * all tests and individual tests in this application.
 */
public class HelpInstrumentationTest extends ActivityInstrumentationTestCase2<HelpActivity>  {

    private TextView sslText;
    
    public HelpInstrumentationTest() {
        super(HelpActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(new Intent(getInstrumentation().getContext(), HelpActivity.class));
        final HelpActivity a = getActivity();
        assertNotNull(a);
        sslText = (TextView) a.findViewById(R.id.ssl_text);
    }
    
    @MediumTest
    public void testPreconditions() {
        assertFalse("SSL text must be populated", sslText.getText().toString().isEmpty());
    }
}
