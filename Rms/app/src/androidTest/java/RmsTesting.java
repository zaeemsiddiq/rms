import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import au.edu.monash.rms.activities.MainActivity;
import au.edu.monash.rms.data.Constants;
import au.edu.monash.rms.data.RouteStop;
import au.edu.monash.rms.data.Stop;
import au.edu.monash.rms.fragments.PlanFragment;

/**
 * Created by Zaeem on 6/10/2016.
 */
@RunWith(AndroidJUnit4.class)

public class RmsTesting extends android.test.ActivityInstrumentationTestCase2<MainActivity> {
    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    MainActivity myFragmentActivity;
    public RmsTesting() {
        super(MainActivity.class);
    }

    @Test
    public void testCheckRoute() {
        assertNotNull(Constants.planFragment);
        myFragmentActivity = (MainActivity) mActivityRule.getActivity();
        // stop 1 and 5 are on route 1 and should be the same
        Stop src = Constants.dbContext.getStop(1);
        Stop dst = Constants.dbContext.getStop(5);

        assertNotNull(src);
        assertNotNull(dst);
        RouteStop routeStopSrc = Constants.dbContext.getRouteStopByStopID(src.getStopID());
        RouteStop routeStopDst = Constants.dbContext.getRouteStopByStopID(dst.getStopID());

        assertEquals(routeStopSrc.getRouteID(), routeStopDst.getRouteID()); // both should have same route ids
    }

    @Test
    public void checkInternet() {
        MainActivity mainActivity = (MainActivity) mActivityRule.getActivity(); // getting the context of main activity

        assertNotNull(mainActivity);    // check if its valid
        assertEquals(true, mainActivity.connectingToInternet());    // checking internet should return true
    }
}
