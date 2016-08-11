package au.edu.monash.rms.data;

/**
 * Created by Zaeem on 5/10/2016.
 */
public class Settings {
    public static final String TABLE_NAME = "Settings";
    public static final String COLUMN_SETTINGS_ID = "ID";
    public static String COLUMN_VIBRATE = "Vibrate";
    public static String COLUMN_RADIUS = "Radius";

    public static final String CREATE_STMT = "CREATE TABLE "+TABLE_NAME+
            "("+
            COLUMN_SETTINGS_ID + " INTEGER, "+
            COLUMN_VIBRATE +" INTEGER, "+ // stores 0 or 1 values
            COLUMN_RADIUS +" INTEGER"+  // stores the searching radius
            ");";
    public static final String INSERT_STMT = "INSERT INTO "+TABLE_NAME+
            "("+
            COLUMN_SETTINGS_ID + ", "+
            COLUMN_VIBRATE +", "+ // stores 0 or 1 values
            COLUMN_RADIUS +""+  // stores the searching radius
            ") VALUES " +
            "(1,1,100);";

    public int SettingsID;
    public int Vibrate;
    public int  Radius;

    public Settings() {

    }

    public int getSettingsID() {
        return SettingsID;
    }

    public void setSettingsID(int settingsID) {
        SettingsID = settingsID;
    }

    public int getVibrate() {
        return Vibrate;
    }

    public void setVibrate(int vibrate) {
        Vibrate = vibrate;
    }

    public int getRadius() {
        return Radius;
    }

    public void setRadius(int radius) {
        Radius = radius;
    }
}
