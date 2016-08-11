package au.edu.monash.rms.fragments;
/*
DISCLAIMER !
This fragment consists of code that inflates a custom info window which contains stops information and a travel here button
The button has a click event registered with it. Since this is not built in feature of native google maps so i took this code
from a stackoverflow user "chose007" mentioned here http://stackoverflow.com/questions/14123243/google-maps-android-api-v2-interactive-infowindow-like-in-original-android-go
i did some tweaks to that code such has implementation of my own custom button.

The bus map marker and splash screen image is taken from the website http://www.flaticon.com/free-icons/bus_174

 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import au.edu.monash.rms.R;
import au.edu.monash.rms.activities.MainActivity;
import au.edu.monash.rms.data.Constants;
import au.edu.monash.rms.data.Stop;
import au.edu.monash.rms.utils.MapWrapperLayout;
import au.edu.monash.rms.utils.OnInfoWindowElemTouchListener;

public class MapFragment extends Fragment {
    private GoogleMap mMap;
    private Context context;

    MainActivity listener;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(Context context, List<Stop> stopList, MainActivity listener) {
        MapFragment fragment = new MapFragment();
        fragment.listener = listener;
        return fragment;
    }

    public void addMarker(Stop stop, LatLng position) {
        MarkerOptions options = new MarkerOptions();
        options.position(position);
        options.title(stop.getStopName());
        options.snippet(String.valueOf(stop.getStopID()));
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        // Add marker to map !
        mMap.addMarker(options);
    }

    public void moveCamera(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, Constants.MAP_ZOOM);
        mMap.animateCamera(cameraUpdate);
        Constants.LAST_LOCATION = location;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;
    MapWrapperLayout mapWrapperLayout;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main, container, false);


        final com.google.android.gms.maps.MapFragment mapFragment = (com.google.android.gms.maps.MapFragment)getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapWrapperLayout = (MapWrapperLayout) v.findViewById(R.id.map_relative_layout);
        mMap = mapFragment.getMap();

        mapWrapperLayout.init(mMap, getPixelsFromDp(getActivity(), (39+20)));

        this.infoWindow = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.info_window, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView)infoWindow.findViewById(R.id.snippet);
        this.infoButton = (Button)infoWindow.findViewById(R.id.button);
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton)
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                //marker button pressed, tell main activity to start travelling here
                Stop stop = Constants.dbContext.getStop( Integer.valueOf(marker.getSnippet()) );
                listener.TravelHere(stop);
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mMap.setMyLocationEnabled(true);
        for(Stop s: Constants.stopList) {
            addMarker(s,new LatLng(s.getStopLatitude(),s.getStopLongitude()));
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());   // this is the id of the stop which need not be shown
                infoSnippet.setVisibility(View.INVISIBLE);
                infoButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //mMapView.getMapAsync(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

}