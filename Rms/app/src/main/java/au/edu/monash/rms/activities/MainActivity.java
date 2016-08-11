package au.edu.monash.rms.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;

import android.location.LocationListener;

import java.util.ArrayList;
import java.util.List;

import au.edu.monash.rms.R;
import au.edu.monash.rms.data.Settings;
import au.edu.monash.rms.fragments.FavoritesFragment;
import au.edu.monash.rms.fragments.JourneyInfoFragment;
import au.edu.monash.rms.fragments.MapFragment;
import au.edu.monash.rms.fragments.PlanFragment;
import au.edu.monash.rms.fragments.SettingsFragment;
import au.edu.monash.rms.service.FragmentData;
import au.edu.monash.rms.service.SyncServiceComplete;
import au.edu.monash.rms.data.Constants;
import au.edu.monash.rms.data.DatabaseHelper;
import au.edu.monash.rms.data.Route;
import au.edu.monash.rms.data.RouteStop;
import au.edu.monash.rms.data.Stop;
import au.edu.monash.rms.service.SyncService;

public class MainActivity extends AppCompatActivity implements SyncServiceComplete, LocationListener, FragmentData {

    private LocationManager locationManager;
    private String provider;

    private TabLayout tabLayoutDashboard;
    LinearLayout loadingFrame;
    LinearLayout displayFrame;

    private ProgressBar syncProgress;
    private int totalSyncObjects = Constants.TOTAL_SYNC_OBJECTS;
    private int doneSyncObjects = 0;

    FrameLayout mainFrame;
    Fragment currentFragment;

    Stop src;   // this is the src stop returned from Plan Fragment
    Stop dst;   // this is the dst stop returned from Plan Fragment
    List<RouteStop> routeStops; // planned stops list that the bus will iterate sequentially
    Stop nextStop;  // next stop that will be calculated by this activity on the basis of location
    Stop previousStop;  // previous stop that the bus passed from
    int stopsWalker;    // this is the counter of stops used to iterate through the list (initially will be set to 0 when the plan starts


    private void generateNotification(String message) { //
        if(Constants.SETTINGS_VIBRATE) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 100 milliseconds
            v.vibrate(100);
        }
        if(!Constants.IS_APPLICATION_MINIMIZED) {   // if app is not minimized display alert box
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Reminder")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // cancel reminder
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } // display status notification code taken from developers.google.com
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_plan)
                        .setContentTitle("Reminder")
                        .setContentText(message);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = getIntent();

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initiateLayout();   // initiating views
        initiateConstants();
        initiateTabsLayout();
        initiateLocationManager();
        loadSettings();
        if(!connectingToInternet()) {
            showDialog(); // ask user if they want to turn on their internet
        } else {
            startFireBaseSync();
        }

    }
    private void showDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);   // got settings
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        finish();
                }
            }
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Your device is not connected to the internet")
                .setPositiveButton("Network settings", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0) {
            if(!connectingToInternet()) {
                showDialog();
            }
            else {
                startFireBaseSync();
            }
        } else if (requestCode == 1) {

        }

    }

    private void loadSettings() {
        Settings settings = Constants.dbContext.getSettings();
        if(settings.getVibrate() == 1){ // vibration is on
            Constants.SETTINGS_VIBRATE = true;
        } else {
            Constants.SETTINGS_VIBRATE = false;
        }
        Constants.SETTINGS_TARGET_RADIUS = settings.getRadius();
    }
    private void initiateLayout() {
        syncProgress = (ProgressBar) findViewById(R.id.mainProgressBar);
        loadingFrame = (LinearLayout) findViewById(R.id.loadingFrame);
        displayFrame = (LinearLayout) findViewById(R.id.displayFrame);
        mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
    }
    private void initiateConstants() {
        Constants.IS_PLAN_SET = false;  // bool var that tells us whether a plan is set or not
        Constants.IS_APPLICATION_MINIMIZED = false; // if app is minimised
        Constants.dbContext = new DatabaseHelper(getApplicationContext());  // init database
        Constants.mainActivity = this;  // setting main object to constant var
        Firebase.setAndroidContext(getApplicationContext()); // init firebase
        Constants.stopList = new ArrayList<Stop>(); // init arrays
        Constants.routeList = new ArrayList<Route>();
        Constants.routeStopList = new ArrayList<RouteStop>();

        // initiate the fragments
        if(Constants.journeyInfoFragment == null) {
            Constants.journeyInfoFragment = (JourneyInfoFragment) JourneyInfoFragment.newInstance(this);
        }
        if(Constants.planFragment == null) {
            Constants.planFragment = (PlanFragment) PlanFragment.newInstance(this);
        }
        if(Constants.settingsFragment == null) {
            Constants.settingsFragment = (SettingsFragment) SettingsFragment.newInstance();
        }
    }
    private void initiateLocationManager() {
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }
    }
    private void initiateTabsLayout() { // adding the tabs dynamically
        tabLayoutDashboard = (TabLayout) findViewById(R.id.mainTabs);
        tabLayoutDashboard.addTab(tabLayoutDashboard.newTab().setText("Map").setIcon(R.drawable.icon_map)); //0
        tabLayoutDashboard.addTab(tabLayoutDashboard.newTab().setText("Plan Journey").setIcon(R.drawable.icon_plan)); //1
        tabLayoutDashboard.addTab(tabLayoutDashboard.newTab().setText("Information").setIcon(R.drawable.icon_info)); //2
        tabLayoutDashboard.addTab(tabLayoutDashboard.newTab().setText("Favourites").setIcon(R.drawable.icon_favorite)); //3
        tabLayoutDashboard.addTab(tabLayoutDashboard.newTab().setText("Settings").setIcon(R.drawable.icon_settings)); //4
        tabLayoutDashboard.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayoutDashboard.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    if(Constants.mapFragment != null) {
                        // hide the current fragment first
                        hideFragment(currentFragment);

                        // add/show the fragment
                        showFragment(Constants.mapFragment);
                        // set the current fragment to this one
                        currentFragment = Constants.mapFragment;
                    }
                } else if (tab.getPosition() == 1) {

                    selectTab(Constants.FRAGMENT_PLAN, true);


                } else if (tab.getPosition() == 2) {
                    // hide the current fragment first
                    hideFragment(currentFragment);

                    // set the current fragment to this one
                    currentFragment = Constants.journeyInfoFragment;

                    // add/show the fragment
                    showFragment(Constants.journeyInfoFragment);

                } else if (tab.getPosition() == 3) {
                    // hide the current fragment first
                    hideFragment(currentFragment);

                    // initiate the next fragment if its not been initiated
                    if(Constants.favoritesFragment == null) {
                        Constants.favoritesFragment = (FavoritesFragment) FavoritesFragment.newInstance(MainActivity.this);
                    }
                    // set the current fragment to this one
                    currentFragment = Constants.favoritesFragment;

                    // add/show the fragment
                    showFragment(Constants.favoritesFragment);

                } else if (tab.getPosition() == 4) {
                    // hide the current fragment first
                    hideFragment(currentFragment);

                    // initiate the next fragment if its not been initiated
                    if(Constants.settingsFragment == null) {
                        Constants.settingsFragment = (SettingsFragment) SettingsFragment.newInstance();
                    }
                    // set the current fragment to this one
                    currentFragment = Constants.settingsFragment;

                    // add/show the fragment
                    showFragment(Constants.settingsFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
    private void startFireBaseSync() {
        SyncService syncService = new SyncService(this);    // start sync procedure
        syncService.syncStops();
    }
    @Override
    public void stopSyncComplete() {// delegates called from SyncService upon completion of task
        doneSyncObjects++;
        syncProgress.setProgress(getProgressPercentage(doneSyncObjects));

        SyncService syncService = new SyncService(this);
        syncService.syncRoutes();
    }

    @Override
    public void routeSyncComplete() {
        doneSyncObjects++;
        syncProgress.setProgress(getProgressPercentage(doneSyncObjects));
        SyncService syncService = new SyncService(this);
        syncService.syncRouteStops();
    }

    @Override
    public void routeStopSyncComplete() {
        doneSyncObjects++;
        syncProgress.setProgress(getProgressPercentage(doneSyncObjects));

        loadingFrame.setVisibility(View.GONE);  // hide the loading frame
        displayFrame.setVisibility(View.VISIBLE);   // load the main frame for application

        Constants.mapFragment = MapFragment.newInstance(this, Constants.stopList, this);  // initialise map fragment
        showFragment(Constants.mapFragment);    // add the fragment to the mainframe i.e. main frame will hold all the fragments
        currentFragment = Constants.mapFragment;    // set the current fragment to map fragment so that it can be hide/ shown later on
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("**","App is minimised");
        Constants.IS_APPLICATION_MINIMIZED = true;  // app is minimised
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 2000, 1, MainActivity.this);
        Constants.IS_APPLICATION_MINIMIZED = false;
        Log.d("asd","Resumed");
    }

    private void fullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }   // code to hide the nav bar and make app full screen

    private void hideFragment(Fragment fragment){
        if(fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(fragment);
            ft.commit();
        }
    }
    private void showFragment(Fragment fragment) {
        if(!fragment.isAdded()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.mainFrame, fragment);
            ft.commit();
        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.show(fragment);
            ft.commit();
        }
    }

    @Override
    public void onLocationChanged(Location location) {  // this event is fired each time the location gets changed
        float lat = (float) (location.getLatitude());
        float lng = (float) (location.getLongitude());
        if(Constants.mapFragment!=null) {
            Constants.mapFragment.moveCamera(location); // move camera to current location

            if(Constants.IS_PLAN_SET) { // plan is set compute the locations here

                float[] results = new float[1]; // initialising a 1d result array to pass into distanceBetween method (source: developers.google.com)
                if(stopsWalker == 0) {
                    Location.distanceBetween(lat,lng,previousStop.getStopLatitude(),previousStop.getStopLongitude(),results); // in case of 0 previous stop is the starting stop
                } else {
                    Location.distanceBetween(lat,lng,nextStop.getStopLatitude(),nextStop.getStopLongitude(),results); // if the bus has already covered first stop then we should be looking for the next stop
                }
                Log.d("*******StopsWalker", String.valueOf(stopsWalker)+ "- distance "+results[0]);

                if(results[0] < Constants.SETTINGS_TARGET_RADIUS) { // we just entered crossed our first stop on the list

                    if(stopsWalker == routeStops.size()-1) {    // means that we just have approached our final stop cancel the plan and notify the user
                        generateNotification(Constants.NOTIFICATION_ARRIVED);
                        StopPlan();
                    } else if(stopsWalker == routeStops.size() -2) {
                        // we have reached second last stop handle notifications here (Start reminding)
                        generateNotification(Constants.NOTIFICATION_SECOND_LAST_STOP);
                        stopsWalker++;
                    } else {
                        stopsWalker++;  // keep going ahead
                    }

                    this.previousStop = routeStops.get( stopsWalker - 1 ).getStop();
                    this.nextStop = routeStops.get( stopsWalker ).getStop();
                    updateJourneyPlanViews();
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onBackPressed() {   // this is fired if user presses the back button. its a good idea to ask the user before quitting the app
        new AlertDialog.Builder(this)
                .setTitle("Caution")
                .setMessage("Do you want to exit the application ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid()); // killing this app
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.button_onoff_indicator_on)
                .show();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void StartPlan(Stop src, Stop dst, List<RouteStop> routeStopList) {
        this.src = src; // from
        this.dst = dst; // to
        this.routeStops = routeStopList;    // total stops between from and to
        this.stopsWalker = 0;   // start traversing from stop 0
        this.previousStop = src; // 0 is the previous stop as we cant have -1 as previous
        this.nextStop = routeStops.get( stopsWalker+1 ).getStop(); //1 as next

        Constants.IS_PLAN_SET = true;   // setting the handler

        Toast.makeText(MainActivity.this, "Plan Started", Toast.LENGTH_SHORT).show();
        selectTab(Constants.FRAGMENT_INFORMATION, false);   // goto info screen to let the user inform about journey progress
        updateJourneyPlanViews();
    }
    private void updateJourneyPlanViews() { // update views
        Constants.journeyInfoFragment.updateViews(
                this.nextStop.getStopName(),
                this.previousStop.getStopName(),
                String.valueOf(routeStops.size() - stopsWalker),
                dst.getStopName(),
                routeStops.size(),
                stopsWalker
        );
    }

    @Override
    public void StopPlan() {
        Constants.IS_PLAN_SET = false;
        Constants.journeyInfoFragment.cleanView();
        selectTab(Constants.FRAGMENT_MAP, false);
    }

    @Override
    public void PrePlanStart(Stop src, Stop dst) {  // this is fired from favorites fragment. and it automatically fills in src and dst into plan fragment
        Constants.planFragment.PrePlanStart(src, dst);
        selectTab(Constants.FRAGMENT_PLAN, false);
    }

    @Override
    public void TravelHere(Stop dst) {  // fired from map where we only know the dst but not the src, usr will input src
        selectTab(Constants.FRAGMENT_PLAN, false);
        Constants.planFragment.TravelHere(dst);
    }
    public void selectTab(int fragmentNumber, boolean cleanView) {
        if(fragmentNumber == Constants.FRAGMENT_MAP) {
            hideFragment(currentFragment);
            showFragment(Constants.mapFragment);
            currentFragment = Constants.mapFragment;
        } else  if(fragmentNumber == Constants.FRAGMENT_PLAN) {
            if(cleanView) {
                Constants.planFragment.cleanView(); // clean the view values
            }
            hideFragment(currentFragment);
            showFragment(Constants.planFragment);
            currentFragment = Constants.planFragment;
        } else  if(fragmentNumber == Constants.FRAGMENT_INFORMATION) {
            hideFragment(currentFragment);
            showFragment(Constants.journeyInfoFragment);
            currentFragment = Constants.journeyInfoFragment;
        }  if(fragmentNumber == Constants.FRAGMENT_FAVORITE) {
            hideFragment(currentFragment);
            showFragment(Constants.favoritesFragment);
            currentFragment = Constants.favoritesFragment;
        }  if(fragmentNumber == Constants.FRAGMENT_SETTINGS) {
            hideFragment(currentFragment);
            showFragment(Constants.settingsFragment);
            currentFragment = Constants.settingsFragment;
        }
        TabLayout.Tab tab = tabLayoutDashboard.getTabAt(fragmentNumber);
        tab.select();
    }


    private int getProgressPercentage(int n) {
        double numer = (double) n;
        double denom = (double) totalSyncObjects;
        double d = ((numer / denom) * 100);
        int progress = (int) d;
        return progress;
    }
    public boolean connectingToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network",
                                    "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }   // checking if we are able to connect to internet source: http://stackoverflow.com/questions/32242384/getallnetworkinfo-is-deprecated-how-to-use-getallnetworks-to-check-network
}
