package au.edu.monash.rms.data;

/**
 * Created by Zaeem on 5/10/2016.
 */
public class RouteStop {
    public static final String TABLE_NAME = "RouteStop";
    public static final String COLUMN_ROUTE_ID = "RouteID";
    public static final String COLUMN_STOP_ID = "StopID";
    public static String COLUMN_STOP_ORDER = "StopOrder";

    public static final String CREATE_STMT = "CREATE TABLE "+TABLE_NAME+
            "("+
            COLUMN_ROUTE_ID + " INTEGER,"+
            COLUMN_STOP_ID +" INTEGER,"+
            COLUMN_STOP_ORDER+" INTEGER"+
            ")";
    public Route Route;
    public Stop Stop;

    public int RouteID;
    public int StopID;
    public int StopOrder;


    public int getRouteID() {
        return RouteID;
    }

    public void setRouteID(int routeID) {
        RouteID = routeID;
    }

    public int getStopID() {
        return StopID;
    }

    public void setStopID(int stopID) {
        StopID = stopID;
    }

    public RouteStop() {  }

    public Route getRoute() {
        return Route;
    }

    public void setRoute(Route route) {
        Route = route;
    }

    public Stop getStop() {
        return Stop;
    }

    public void setStop(Stop stop) {
        Stop = stop;
    }

    public int getStopOrder() {
        return StopOrder;
    }

    public void setStopOrder(int stopOrder) {
        StopOrder = stopOrder;
    }
}
