package com.kedzie.vbox.test.activity;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.server.HelpActivity;

/**
 * This demonstrates completely isolated "unit test" of an Activity class.
 * <p>
 * This model for testing creates the entire Activity (like
 * {@link Focus2ActivityTest}) but does not attach it to the system (for
 * example, it cannot launch another Activity). It allows you to inject
 * additional behaviors via the
 * {@link android.test.ActivityUnitTestCase#setActivityContext(Context)} and
 * {@link android.test.ActivityUnitTestCase#setApplication(android.app.Application)}
 * methods. It also allows you to more carefully test your Activity's
 * performance Writing unit tests in this manner requires more care and
 * attention, but allows you to test very specific behaviors, and can also be an
 * easier way to test error conditions.
 * <p>
 * Because ActivityUnitTestCase creates the Activity under test completely
 * outside of the usual system, tests of layout and point-click UI interaction
 * are much less useful in this configuration. It's more useful here to
 * concentrate on tests that involve the underlying data model, internal
 * business logic, or exercising your Activity's life cycle.
 * <p>
 * See {@link com.kedzie.vbox.test.example.android.apis.AllTests} for
 * documentation on running all tests and individual tests in this application.
 */
public class HelpActivityTest extends ActivityUnitTestCase<HelpActivity> {

    private Intent mStartIntent;
    private TextView mSSLText;

    public HelpActivityTest() {
        super(HelpActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mStartIntent = new Intent(Intent.ACTION_MAIN);
    }

    @MediumTest
    public void testPreconditions() {
        startActivity(mStartIntent, null, null);
        mSSLText = (TextView) getActivity().findViewById(R.id.ssl_text);
        assertNotNull(getActivity());
        assertNotNull(mSSLText);
    }

    /**
     * This test demonstrates ways to exercise the Activity's life cycle.
     */
    @MediumTest
    public void testLifeCycleCreate() {
        HelpActivity activity = startActivity(mStartIntent, null, null);

        // At this point, onCreate() has been called
        getInstrumentation().callActivityOnStart(activity);
        getInstrumentation().callActivityOnResume(activity);

        // At this point you could use a Mock Context to confirm that 
        //your activity has made certain calls to the system & set itself up properly.
        getInstrumentation().callActivityOnPause(activity);

        // At this point you could confirm that the activity has paused
        getInstrumentation().callActivityOnStop(activity);

        // At this point, you could confirm that the activity has shut itself down
        // or you could use a Mock Context to confirm that your activity has
        // released any system resources it should no longer be holding.
    }
}
