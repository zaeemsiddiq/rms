package au.edu.monash.rms.data;

/**
 * Created by Zaeem on 5/10/2016.
 */
public class Favorite {
    public static final String TABLE_NAME = "Favorite";
    public static final String COLUMN_FAVORITE_ID = "FavouriteID";
    public static String COLUMN_FAVORITE_NAME = "FavoriteName";
    public static String COLUMN_SOURCE_STOP_ID = "SourceStopID";
    public static String COLUMN_DESTINATION_STOP_ID = "DestinationStopID";

    public static final String CREATE_STMT = "CREATE TABLE "+TABLE_NAME+
            "( "+
            COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            COLUMN_FAVORITE_NAME +" TEXT, "+
            COLUMN_SOURCE_STOP_ID + " REAL, "+
            COLUMN_DESTINATION_STOP_ID + " REAL"+
            ")";

    public int FavouriteID;
    public String FavoriteName;
    public int SourceStopID;
    public Stop SourceStop;
    public int DestinationStopID;
    public Stop DestinationStop;

    public Favorite() {}

    public int getFavouriteID() {
        return FavouriteID;
    }

    public void setFavouriteID(int favouriteID) {
        FavouriteID = favouriteID;
    }

    public String getFavoriteName() {
        return FavoriteName;
    }

    public void setFavoriteName(String favoriteName) {
        FavoriteName = favoriteName;
    }

    public int getSourceStopID() {
        return SourceStopID;
    }

    public void setSourceStopID(int sourceStopID) {
        SourceStopID = sourceStopID;
    }

    public int getDestinationStopID() {
        return DestinationStopID;
    }

    public void setDestinationStopID(int destinationStopID) {
        DestinationStopID = destinationStopID;
    }

    public Stop getDestinationStop() {
        return DestinationStop;
    }

    public void setDestinationStop(Stop destinationStop) {
        DestinationStop = destinationStop;
    }

    public Stop getSourceStop() {
        return SourceStop;
    }

    public void setSourceStop(Stop sourceStop) {
        SourceStop = sourceStop;
    }
}
