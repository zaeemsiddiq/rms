package au.edu.monash.rms.data;

import android.location.Location;

import java.util.List;

import au.edu.monash.rms.activities.MainActivity;
import au.edu.monash.rms.fragments.FavoritesFragment;
import au.edu.monash.rms.fragments.JourneyInfoFragment;
import au.edu.monash.rms.fragments.MapFragment;
import au.edu.monash.rms.fragments.PlanFragment;
import au.edu.monash.rms.fragments.SettingsFragment;

/**
 * Created by Zaeem on 5/10/2016.
 */
public class Constants {
    public static DatabaseHelper dbContext;
    public static List<Stop> stopList;
    public static List<Route> routeList;
    public static List<RouteStop> routeStopList;

    public static int TOTAL_SYNC_OBJECTS = 3;   // total sync objects (Route, RouteStop and Stop) used to set progress bar progress

    /*Fragment instances*/
    public static MainActivity mainActivity;
    public static MapFragment mapFragment;
    public static SettingsFragment settingsFragment;
    public static PlanFragment planFragment;
    public static JourneyInfoFragment journeyInfoFragment;
    public static FavoritesFragment favoritesFragment;

    public static boolean IS_APPLICATION_MINIMIZED = false;

    public static boolean DIRECTION_UP;
    public static String NOTIFICATION_ARRIVED="You have almost reached your destination";
    public static String NOTIFICATION_SECOND_LAST_STOP = "The next stop is the destination";

    public static boolean IS_PLAN_SET;
    public static int SETTINGS_TARGET_RADIUS;
    public static boolean SETTINGS_VIBRATE;

    /* MAP SETTINGS */
    public static int MAP_ZOOM = 50;
    public static Location LAST_LOCATION;

    public static int FRAGMENT_MAP = 0; // used to map tab positions
    public static int FRAGMENT_PLAN = 1;
    public static int FRAGMENT_INFORMATION = 2;
    public static int FRAGMENT_FAVORITE = 3;
    public static int FRAGMENT_SETTINGS = 4;
}
