package au.edu.monash.rms.data;

/**
 * Created by Zaeem on 5/10/2016.
 */
public class Route {
    public static final String TABLE_NAME = "Route";
    public static final String COLUMN_ROUTE_ID = "RouteID";
    public static String COLUMN_ROUTE_NAME = "RouteName";

    public static final String CREATE_STMT = "CREATE TABLE "+TABLE_NAME+
            "("+
            COLUMN_ROUTE_ID + " INTEGER,"+
            COLUMN_ROUTE_NAME +" TEXT"+
            ")";
    public int RouteID;
    public String RouteName;

    public Route () {

    }

    public String getRouteName() {
        return RouteName;
    }

    public void setRouteName(String routeName) {
        RouteName = routeName;
    }

    public int getRouteID() {
        return RouteID;
    }

    public void setRouteID(int routeID) {
        RouteID = routeID;
    }
}
