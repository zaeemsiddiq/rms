package au.edu.monash.rms.service;

/**
 * Created by Zaeem on 5/10/2016.
 */
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Iterator;
import java.util.List;

import au.edu.monash.rms.activities.MainActivity;
import au.edu.monash.rms.data.Constants;
import au.edu.monash.rms.data.Route;
import au.edu.monash.rms.data.RouteStop;
import au.edu.monash.rms.data.Stop;

public class SyncService {
    Firebase mRootRef;
    MainActivity listener;

    public SyncService(MainActivity listener) { // setting up the delegate so that the service can talk to main activity using this object
        this.listener = listener;
        this.mRootRef = new Firebase("https://remindmystop.firebaseio.com/");   // initiating firebase obj
    }

    public void syncStops() {   // start syncing stops
        Firebase stopRef = mRootRef.child("Stop");
        stopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("There are " + dataSnapshot.getChildrenCount() + " items");
                for (DataSnapshot stop: dataSnapshot.getChildren()) {
                    Stop s = stop.getValue(Stop.class);
                    if(!Constants.dbContext.stopExists(s.getStopID())) {    // add new
                        Constants.dbContext.addStop(s);
                    }
                    else {  // update existing
                        Constants.dbContext.updateStop(s);
                    }

                    Constants.stopList.add(s);
                    System.out.println(s.getStopID() + " - " + s.getStopName());
                }
                listener.stopSyncComplete();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }
    public void syncRoutes() {
        Firebase routeRef = mRootRef.child("Route");
        routeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("There are " + dataSnapshot.getChildrenCount() + " items");
                for (DataSnapshot route: dataSnapshot.getChildren()) {
                    Route r = route.getValue(Route.class);
                    if(!Constants.dbContext.routeExists(r.getRouteID())) {    // add new
                        Constants.dbContext.addRoute(r);
                    }
                    else {  // update existing
                        Constants.dbContext.updateRoute(r);
                    }
                    Constants.routeList.add(r);
                    System.out.println(r.getRouteID() + " - " + r.getRouteName());
                }
                listener.routeSyncComplete();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }
    public void syncRouteStops() {
        Firebase routeStopRef = mRootRef.child("RouteStop");
        routeStopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("There are " + dataSnapshot.getChildrenCount() + " items");
                for (DataSnapshot routeStop: dataSnapshot.getChildren()) {
                    RouteStop rS = routeStop.getValue(RouteStop.class);
                    if(!Constants.dbContext.routeStopExists(rS.getStopID(),rS.getRouteID())) {
                        Constants.dbContext.addRouteStop(rS);
                    } else {
                        Constants.dbContext.updateRouteStop(rS);
                    }
                    Constants.routeStopList.add(rS);
                    System.out.println(rS.getStopOrder());
                }
                listener.routeStopSyncComplete();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }
}
