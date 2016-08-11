package au.edu.monash.rms.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import au.edu.monash.rms.R;
import au.edu.monash.rms.activities.MainActivity;
import au.edu.monash.rms.data.Constants;

public class JourneyInfoFragment extends Fragment {

    String nS, pS, sTD, d;  // temp vars which will hold the values from main if the fragment view is not loaded as yet

    int totalStops, stopsCovered;

    TextView nextStop;
    TextView previousStop;
    TextView stopsToDst;
    TextView dst;
    ProgressBar progressBar;
    Button cancelButton;

    private boolean isViewLoaded = false;
    boolean pendingValues = false;  // a flag which will decide if there are any pending values that need to be inflated in views on createView

    MainActivity listener;
    public JourneyInfoFragment() {
    }
    public static JourneyInfoFragment newInstance(MainActivity listener) {
        JourneyInfoFragment fragment = new JourneyInfoFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journey_info, container, false);
        initiateView(view);
        if(pendingValues) { // load any pending values if any
            updateViews(nS,pS,sTD,d, totalStops, stopsCovered);
        }
        return view;
    }
    private void initiateView(View view) {
        previousStop = (TextView) view.findViewById(R.id.journeyTextPreviousStop);
        nextStop = (TextView) view.findViewById(R.id.journeyTextNextStop);
        stopsToDst = (TextView) view.findViewById(R.id.journeyTextStopsToDest);
        dst = (TextView) view.findViewById(R.id.journeyTextDest);
        cancelButton = (Button) view.findViewById(R.id.journeyButtonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Constants.IS_PLAN_SET) {
                    listener.StopPlan();
                } else {
                    Toast.makeText(getActivity(), "No Plan Set", Toast.LENGTH_SHORT).show();
                }
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.journeyProgress);
        isViewLoaded = true;
        cleanView();
    }

    public void updateViews(String nxtStop, String prevStop, String stpsToDst, String dest, int totalStops, int stopsCovered) {
        if(!isViewLoaded) { // view is not loaded yet store the values in temp vars
            pendingValues = true;
            nS = nxtStop;
            pS = prevStop;
            sTD = stpsToDst;
            d = dest;
            this.totalStops = totalStops;
            this.stopsCovered = stopsCovered;
        } else {
            previousStop.setText(prevStop);
            nextStop.setText(nxtStop);
            stopsToDst.setText(stpsToDst);
            dst.setText(dest);
            this.totalStops = totalStops;
            this.stopsCovered = stopsCovered;
            progressBar.setProgress(getProgressPercentage(stopsCovered));
            Log.d("-->",getProgressPercentage(stopsCovered)+":"+stopsCovered);
        }
    }
    private int getProgressPercentage(int n) {
        double numer = (double) n;
        double denom = (double) totalStops;
        double d = ((numer / denom) * 100);
        int progress = (int) d;
        return progress;
    }
    public void cleanView() {   // reset all the values
        if(progressBar != null) {
            progressBar.setProgress(0);
        }
        if(stopsToDst != null) {
            stopsToDst.setText("No Route Set");
        }
        if(nextStop!= null) {
            nextStop.setText("No Route Set");
        }
        if(dst != null) {
            dst.setText("No Route Set");
        }
        if(previousStop != null) {
            previousStop.setText("No Route Set");
        }
        if(cancelButton != null) {
            cancelButton.setText("No Route Set");
        }
    }
}
