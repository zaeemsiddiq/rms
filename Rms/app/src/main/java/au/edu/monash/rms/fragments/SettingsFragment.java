package au.edu.monash.rms.fragments;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import au.edu.monash.rms.R;
import au.edu.monash.rms.activities.About;
import au.edu.monash.rms.data.Constants;
import au.edu.monash.rms.data.Settings;

public class SettingsFragment extends Fragment {

    Settings settings;
    Switch vibrateSwitch;
    EditText radiusText;
    Button buttonSave;
    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.settings = Constants.dbContext.getSettings();
        return settingsFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initiateView(view);
        return view;
    }
    private void initiateView(View view) {
        ImageButton aboutButton = (ImageButton) view.findViewById(R.id.settingsAboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), About.class);
                startActivityForResult(intent, 1);  // start about activity
            }
        });
        vibrateSwitch = (Switch) view.findViewById(R.id.settingsSwitchVibrate);
        radiusText = (EditText) view.findViewById(R.id.settingsTextDistance);
        buttonSave = (Button) view.findViewById(R.id.settingsButtonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings newSettings = new Settings();
                newSettings.setSettingsID(1);
                if(vibrateSwitch.isChecked()) { // mapping the settings objects
                    newSettings.setVibrate(1);
                    Constants.SETTINGS_VIBRATE = true;
                } else {
                    newSettings.setVibrate(0);
                    Constants.SETTINGS_VIBRATE = false;
                }
                if(radiusText.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please enter the radius", Toast.LENGTH_SHORT).show();
                    return;
                }
                newSettings.setRadius(Integer.parseInt(radiusText.getText().toString()));
                Constants.SETTINGS_TARGET_RADIUS = Integer.parseInt(radiusText.getText().toString());
                Constants.dbContext.updateSettings(newSettings);
                Toast.makeText(getActivity(), "New Settings saved", Toast.LENGTH_SHORT).show();
            }
        });
        radiusText.setText(String.valueOf(this.settings.getRadius()) );
        if(this.settings.getVibrate() == 1) {
            vibrateSwitch.setChecked(true);
        } else {
            vibrateSwitch.setChecked(false);
        }
    }
}
