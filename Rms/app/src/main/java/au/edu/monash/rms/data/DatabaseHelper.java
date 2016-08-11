package au.edu.monash.rms.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zaeem on 5/10/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "rms";
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Stop.CREATE_STMT);
            db.execSQL(Route.CREATE_STMT);
            db.execSQL(RouteStop.CREATE_STMT);
            db.execSQL(Favorite.CREATE_STMT);
            db.execSQL(Settings.CREATE_STMT);
            db.execSQL(Settings.INSERT_STMT);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Stop.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Route.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RouteStop.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Favorite.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Settings.TABLE_NAME);
        onCreate(db);
    }

    public Favorite getFavorite(int favoriteID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Favorite.TABLE_NAME+" WHERE "+Favorite.COLUMN_FAVORITE_ID+"="+favoriteID, null);
        if (cursor.moveToFirst()) {
            Favorite favorite = new Favorite();
            favorite.setFavouriteID(cursor.getInt(0));
            favorite.setFavoriteName(cursor.getString(1));
            favorite.setSourceStopID(cursor.getInt(2));
            favorite.setDestinationStopID(cursor.getInt(3));

            favorite.setSourceStop(getStop(cursor.getInt(2)));
            favorite.setDestinationStop(getStop(cursor.getInt(3)));
            return favorite;
        }
        else {
            return null;
        }
    }
    public List<Favorite> getAllFavorites() {
        List<Favorite> favorites = new ArrayList<Favorite>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Favorite.TABLE_NAME, null);
        if(cursor.moveToFirst()) {
            do {
                Favorite favorite = new Favorite();
                favorite.setFavouriteID(cursor.getInt(0));
                favorite.setFavoriteName(cursor.getString(1));
                favorite.setSourceStopID(cursor.getInt(2));
                favorite.setDestinationStopID(cursor.getInt(3));

                favorite.setSourceStop(getStop(cursor.getInt(2)));
                favorite.setDestinationStop(getStop(cursor.getInt(3)));

                favorites.add(favorite);
            }while (cursor.moveToNext());
            return favorites;
        } else {
            return null;
        }
    }
    public void deleteFavorite(int favoriteID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Favorite.TABLE_NAME, Favorite.COLUMN_FAVORITE_ID+"="+favoriteID,null);
    }
    public boolean isFavorite(int sourceStopID, int destinationStopID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Favorite.TABLE_NAME+" WHERE "+Favorite.COLUMN_SOURCE_STOP_ID+"="+sourceStopID+
                " AND "+Favorite.COLUMN_DESTINATION_STOP_ID+"="+destinationStopID, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }
    }
    public void addFavorite(Favorite favorite){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Favorite.COLUMN_FAVORITE_NAME, favorite.getFavoriteName());
        values.put(Favorite.COLUMN_SOURCE_STOP_ID, favorite.getSourceStopID());
        values.put(Favorite.COLUMN_DESTINATION_STOP_ID, favorite.getDestinationStopID());
        db.insert(Favorite.TABLE_NAME, null, values);
        db.close();
    }

    public void updateSettings(Settings settings) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Settings.COLUMN_VIBRATE, settings.getVibrate());
        values.put(Settings.COLUMN_RADIUS, settings.getRadius());
        db.update(Settings.TABLE_NAME, values, Settings.COLUMN_SETTINGS_ID +"="+settings.getSettingsID(),null);
    }
    public Settings getSettings() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Settings.TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            Settings settings = new Settings();
            settings.setSettingsID(cursor.getInt(0));
            settings.setVibrate(cursor.getInt(1));
            settings.setRadius(cursor.getInt(2));
            return settings;
        }
        else {
            return null;
        }
    }

    public Stop getStop(int stopID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Stop.TABLE_NAME+" WHERE "+Stop.COLUMN_STOP_ID+"="+stopID, null);
        if (cursor.moveToFirst()) {
            Stop stop = new Stop();
            stop.setStopID(cursor.getInt(0));
            stop.setStopName(cursor.getString(1));
            stop.setStopLatitude(cursor.getDouble(2));
            stop.setStopLongitude(cursor.getDouble(3));
            return stop;
        }
        else {
            return null;
        }
    }
    public void addStop(Stop stop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Stop.COLUMN_STOP_ID, stop.getStopID());
        values.put(Stop.COLUMN_STOP_NAME, stop.getStopName());
        values.put(Stop.COLUMN_STOP_LATITUDE, stop.getStopLatitude());
        values.put(Stop.COLUMN_STOP_LONGITUDE, stop.getStopLongitude());
        db.insert(Stop.TABLE_NAME, null, values);
        db.close();
    }

    public boolean stopExists(int stopID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Stop.TABLE_NAME+" WHERE "+Stop.COLUMN_STOP_ID+"="+stopID, null);
        if (cursor.moveToFirst()) {
           return true;
        }
        else {
            return false;
        }
    }
    public void updateStop(Stop stop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Stop.COLUMN_STOP_NAME, stop.getStopName());
        values.put(Stop.COLUMN_STOP_LATITUDE, stop.getStopLatitude());
        values.put(Stop.COLUMN_STOP_LONGITUDE, stop.getStopLongitude());
        db.update(Stop.TABLE_NAME,values,Stop.COLUMN_STOP_ID + "=" +stop.getStopID(), null);
    }

    public void addRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Route.COLUMN_ROUTE_ID, route.getRouteID());
        values.put(Route.COLUMN_ROUTE_NAME, route.getRouteName());
        db.insert(Route.TABLE_NAME, null, values);
        db.close();
    }

    public boolean routeExists(int routeID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Route.TABLE_NAME+" WHERE "+Route.COLUMN_ROUTE_ID+"="+routeID, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void updateRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Route.COLUMN_ROUTE_NAME, route.getRouteName());
        db.update(Route.TABLE_NAME, values, Route.COLUMN_ROUTE_ID +"="+route.getRouteID(),null);
    }

    public void addRouteStop(RouteStop routeStop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RouteStop.COLUMN_ROUTE_ID, routeStop.getRouteID());
        values.put(RouteStop.COLUMN_STOP_ID, routeStop.getStopID());
        values.put(RouteStop.COLUMN_STOP_ORDER, routeStop.getStopOrder());
        db.insert(RouteStop.TABLE_NAME, null, values);
        db.close();
    }

    public RouteStop getRouteStopByStopID(int stopID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + RouteStop.TABLE_NAME+" WHERE "+RouteStop.COLUMN_STOP_ID+"="+stopID, null);
        if (cursor.moveToFirst()) { // returns only one stopID
            RouteStop routeStop = new RouteStop();
            routeStop.setRouteID(cursor.getInt(0));
            routeStop.setStopID(cursor.getInt(1));
            routeStop.setStopOrder(cursor.getInt(2));
            return routeStop;
        }
        else {
            return null;
        }
    }
    public List<RouteStop> getAllRouteStopsBetweenSrcDst(int routeID, int srcStopOrder, int dstStopOrder, boolean directionUP) {
        List<RouteStop> routeStops = new ArrayList<RouteStop>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if(directionUP) {   // direction is up. get stops greater than src and less than dst
            cursor = db.rawQuery("SELECT * FROM " + RouteStop.TABLE_NAME+
                    " WHERE "+RouteStop.COLUMN_ROUTE_ID+"="+routeID+
                    " AND "+RouteStop.COLUMN_STOP_ORDER+">="+ srcStopOrder+
                    " AND "+RouteStop.COLUMN_STOP_ORDER+"<="+dstStopOrder, null);
        } else {    // direction is down. get stops less than src and greater than dst
            cursor = db.rawQuery("SELECT * FROM " + RouteStop.TABLE_NAME+
                    " WHERE "+RouteStop.COLUMN_ROUTE_ID+"="+routeID+
                    " AND "+RouteStop.COLUMN_STOP_ORDER+"<="+ srcStopOrder+
                    " AND "+RouteStop.COLUMN_STOP_ORDER+">="+dstStopOrder+
                    " ORDER BY StopOrder DESC", null);
        }
        if (cursor.moveToFirst()) { // returns all stops by route ID
            do {
                RouteStop routeStop = new RouteStop();
                routeStop.setRouteID(cursor.getInt(0));
                routeStop.setStopID(cursor.getInt(1));
                routeStop.setStop(getStop(cursor.getInt(1)));
                routeStop.setStopOrder(cursor.getInt(2));

                routeStops.add(routeStop);
            } while (cursor.moveToNext());

            return routeStops;
        }
        else {
            return null;
        }
    }
    public boolean routeStopExists(int stopID, int routeID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + RouteStop.TABLE_NAME+" WHERE "+RouteStop.COLUMN_ROUTE_ID+"="+routeID+" AND "+RouteStop.COLUMN_STOP_ID+"="+stopID, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void updateRouteStop(RouteStop routeStop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RouteStop.COLUMN_ROUTE_ID, routeStop.getRouteID());
        values.put(RouteStop.COLUMN_STOP_ID, routeStop.getStopID());
        values.put(RouteStop.COLUMN_STOP_ORDER, routeStop.getStopOrder());
        db.update(RouteStop.TABLE_NAME, values, RouteStop.COLUMN_ROUTE_ID +"="+routeStop.getRouteID()
                +" AND "+ RouteStop.COLUMN_STOP_ID +"="+routeStop.getStopID(),null);
    }

    public void queryTester() {

    }
}
