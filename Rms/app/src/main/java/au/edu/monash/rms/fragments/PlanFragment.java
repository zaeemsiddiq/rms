package au.edu.monash.rms.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import au.edu.monash.rms.R;
import au.edu.monash.rms.activities.MainActivity;
import au.edu.monash.rms.data.Constants;
import au.edu.monash.rms.data.Favorite;
import au.edu.monash.rms.data.RouteStop;
import au.edu.monash.rms.data.Stop;
import au.edu.monash.rms.listAdapters.StopsListAdapter;
import au.edu.monash.rms.service.BackgroundService;
import au.edu.monash.rms.service.FragmentData;

public class PlanFragment extends Fragment {

    AutoCompleteTextView textViewSrc;
    AutoCompleteTextView textViewDst;
    Button buttonGo;
    Button buttonFavorite;
    StopsListAdapter adapterSrc;
    StopsListAdapter adapterDst;

    Stop src;
    Stop dst;

    FragmentData mainListener;


    public PlanFragment() {
        // Required empty public constructor
    }

    public static PlanFragment newInstance(MainActivity mainActivity) {
        PlanFragment fragment = new PlanFragment();
        fragment.mainListener = mainActivity;   // used to talk back to main activity
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);
        initiateAutoSrc(view);
        initiateAutoDst(view);
        initiateButton(view);
        if(pendingValues) {
            if(src != null) {
                textViewSrc.setText(src.getStopName());
            }
            if(dst != null) {
                textViewDst.setText(dst.getStopName());
            }
        }
        isViewLoaded = true;
        return view;
    }

    public void cleanView() { // this method is called to clean the views on switching the tabs
        this.src = null;
        this.dst = null;
        if(textViewSrc != null){
            textViewSrc.setText("");
        }
        if(textViewDst != null) {
            textViewDst.setText("");
        }

    }
    private void initiateAutoSrc(View view) {
        adapterSrc = new StopsListAdapter(getActivity(), Constants.stopList);
        textViewSrc = (AutoCompleteTextView) view.findViewById(R.id.planAutoSrc);

        textViewSrc.setAdapter(adapterSrc); // my custom adapter
        textViewSrc.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                src = adapterSrc.getItem(arg2);
                textViewSrc.setText(src.getStopName());
            }
        });

        textViewSrc.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0) { // if the text is empty, we need to reset the stops list to original state. do same with dst
                    adapterSrc.resetData();
                }
            }
        });
    }
    private void initiateAutoDst(View view) {
        adapterDst = new StopsListAdapter(getActivity(), Constants.stopList);
        textViewDst = (AutoCompleteTextView) view.findViewById(R.id.planAutoDst);

        textViewDst.setAdapter(adapterDst);
        textViewDst.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                dst = adapterDst.getItem(arg2);
                textViewDst.setText(dst.getStopName());
            }
        });

        textViewDst.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0) {
                    adapterDst.resetData();
                }
            }
        });
    }
    private void initiateButton(View view) {
        buttonFavorite = (Button) view.findViewById(R.id.planButtonFavorite);
        buttonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(src == null || dst == null) {
                    Toast.makeText(getActivity(), "Please enter source and destination", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setView(R.layout.dialog_favorite)
                            .setTitle("Name")
                            .setMessage("Please enter the name of this route")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Dialog f = (Dialog) dialog;
                                    EditText inputTemp = (EditText) f.findViewById(R.id.dialogFavoriteName);

                                    if(inputTemp.length() == 0 || src == null || dst == null){
                                        // validation
                                        Toast.makeText(getActivity(), "Name, source or destination cannot be empty", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        Favorite favorite = new Favorite(); // now that we have the name as well, save the fav route to DB
                                        favorite.setFavoriteName(inputTemp.getText().toString());
                                        favorite.setSourceStopID(src.getStopID());
                                        favorite.setDestinationStopID(dst.getStopID());
                                        Constants.dbContext.addFavorite(favorite);
                                        Constants.favoritesFragment.refresh();
                                    }

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.button_onoff_indicator_on)
                            .show();
                }
            }
        });
        buttonGo = (Button) view.findViewById(R.id.planButtonGo);
        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPlan();
            }
        });
    }

    private void startPlan() {
        // get two valid stops i.e. already set in auto text complete listener src and dst (validate here)
        if( (src == null || textViewSrc.getText().length() == 0) || (dst == null || textViewDst.getText().length() == 0) ) {
            Toast.makeText(getActivity(), "Please Enter Src and Dst Stops", Toast.LENGTH_SHORT).show();
            return;
        }

        RouteStop routeStopSrc = Constants.dbContext.getRouteStopByStopID(src.getStopID());
        RouteStop routeStopDst = Constants.dbContext.getRouteStopByStopID(dst.getStopID());
        if(routeStopDst.getStopOrder() > routeStopSrc.getStopOrder()) { // based on the stop order, get the bus direction, if stop order of dst is greater, which means we will be incrementing next stops from list. else decrement from the list
            Constants.DIRECTION_UP = true;
        } else {
            Constants.DIRECTION_UP = false;
        }
        if(routeStopSrc.getRouteID() == routeStopDst.getRouteID()) {    // both stops are on the same route.
            // if direction is up, means dst stop has GREATER stop order than src. (get ASCENDING ORDERED LIST)
            // else direction is down, means dst stop has LESSER stop order than src (get DESCENDING ORDERED LIST)

            List<RouteStop> routeStops = Constants.dbContext.getAllRouteStopsBetweenSrcDst(1,routeStopSrc.getStopOrder(),routeStopDst.getStopOrder(),Constants.DIRECTION_UP);
            Toast.makeText(getActivity(), ""+routeStops.get(0).getStopOrder()+"-"+routeStops.get(routeStops.size()-1).getStopOrder(), Toast.LENGTH_SHORT).show();

            mainListener.StartPlan(src, dst, routeStops);

        } else {
            Toast.makeText(getActivity(),"Both Stops should be on the same route",Toast.LENGTH_SHORT).show();
            return;
        }
    }


    private boolean isViewLoaded = false;
    boolean pendingValues = false;  // a flag which will decide if there are any pending values that need to be inflated in views on createView

    public void PrePlanStart(Stop src, Stop dst) { // this is used to fill in details automatically (fav fragment to plan frag)
        if(!isViewLoaded) { // view is not loaded yet store the values in temp vars
            pendingValues = true;
            this.src = src;
            this.dst = dst;
        } else {
            this.src = src;
            this.dst = dst;
            textViewSrc.setText(src.getStopName());
            textViewDst.setText(dst.getStopName());
        }
    }
    public void TravelHere(Stop dst) {
        if(!isViewLoaded) { // view is not loaded yet store the values in temp vars
            pendingValues = true;
            this.dst = dst;
        } else {
            this.dst = dst;
            textViewDst.setText(dst.getStopName());
            Toast.makeText(getActivity(), "Please enter yor source stop and press GO", Toast.LENGTH_SHORT).show();
        }
    }
}
