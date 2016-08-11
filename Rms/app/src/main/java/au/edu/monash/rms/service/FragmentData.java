package au.edu.monash.rms.service;

import java.util.List;

import au.edu.monash.rms.data.RouteStop;
import au.edu.monash.rms.data.Stop;

/**
 * Created by Zaeem on 6/8/2016.
 */
public interface FragmentData {
    public void StartPlan(Stop src, Stop dst, List<RouteStop> routeStopList);
    public void StopPlan();
    public void PrePlanStart(Stop src, Stop dst); // this is used to fill in details automatically (fav fragment to plan frag)
    public void TravelHere(Stop dst);// map will call this method to tell activity to start planning with dst as destination and user would have to enter src
}
