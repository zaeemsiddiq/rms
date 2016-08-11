package au.edu.monash.rms.data;

/**
 * Created by Zaeem on 5/10/2016.
 */
public class Stop {
    public static final String TABLE_NAME = "Stop";
    public static final String COLUMN_STOP_ID = "StopID";
    public static String COLUMN_STOP_NAME = "StopName";
    public static String COLUMN_STOP_LATITUDE = "StopLatitude";
    public static String COLUMN_STOP_LONGITUDE = "StopLongitude";

    public static final String CREATE_STMT = "CREATE TABLE "+TABLE_NAME+
            "( "+
            COLUMN_STOP_ID + " INTEGER,"+
            COLUMN_STOP_NAME +" TEXT,"+
            COLUMN_STOP_LATITUDE + " REAL,"+
            COLUMN_STOP_LONGITUDE + " REAL,"+
            "PRIMARY KEY("+COLUMN_STOP_ID+")"+
            ")";

    public int StopID;
    public String StopName;
    public double StopLatitude;
    public double StopLongitude;

    public Stop() {}

    public double getStopLongitude() {
        return StopLongitude;
    }

    public void setStopLongitude(double stopLongitude) {
        StopLongitude = stopLongitude;
    }

    public int getStopID() {
        return StopID;
    }

    public void setStopID(int stopID) {
        StopID = stopID;
    }

    public String getStopName() {
        return StopName;
    }

    public void setStopName(String stopName) {
        StopName = stopName;
    }

    public double getStopLatitude() {
        return StopLatitude;
    }

    public void setStopLatitude(double stopLatitude) {
        StopLatitude = stopLatitude;
    }
}
