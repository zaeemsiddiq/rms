package au.edu.monash.rms.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import au.edu.monash.rms.R;
import au.edu.monash.rms.activities.MainActivity;
import au.edu.monash.rms.data.Constants;
import au.edu.monash.rms.data.Favorite;
import au.edu.monash.rms.listAdapters.FavoriteListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {
    private TextView errorMessageText;
    private ListView listView;
    MainActivity listener;  // used to reference back to main activity
    private FavoriteListAdapter favoriteListAdapter; // adapter to load into list
    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance(MainActivity listener) {
        FavoritesFragment fragment = new FavoritesFragment();
        fragment.listener = listener;   //setting up the listener
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        initialiseListView(view);
        return view;
    }
    public void refresh() { // reload the list
        favoriteListAdapter = new FavoriteListAdapter(getActivity(), Constants.dbContext.getAllFavorites());
        listView.setAdapter(favoriteListAdapter);
        if(favoriteListAdapter.getCount()==0) { // hide/ show the error message
            listView.setVisibility(View.GONE);
            errorMessageText.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            errorMessageText.setVisibility(View.GONE);
        }
        favoriteListAdapter.notifyDataSetChanged();
    }

    private void initialiseListView(View view) {
        errorMessageText = (TextView) view.findViewById(R.id.favoritesTextEmptyMessage);
        errorMessageText.setVisibility(View.GONE);
        listView = (ListView) view.findViewById(R.id.favoritesListView);
        favoriteListAdapter = new FavoriteListAdapter(getActivity(), Constants.dbContext.getAllFavorites());
        listView.setAdapter(favoriteListAdapter);
        if(favoriteListAdapter.getCount()==0) { // hide/ show the error message
            listView.setVisibility(View.GONE);
            errorMessageText.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            errorMessageText.setVisibility(View.GONE);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {    // preplan
                Toast.makeText(getActivity(), position + "clicked", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to travel this route ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Favorite favorite = favoriteListAdapter.getItem(position);
                                listener.PrePlanStart(favorite.getSourceStop(), favorite.getDestinationStop());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

       listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
               new AlertDialog.Builder(getActivity())
                       .setTitle("Confirm")
                       .setMessage("Are you sure you want to delete this route ?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) { // delete
                               Constants.dbContext.deleteFavorite(favoriteListAdapter.getItem(position).getFavouriteID());
                               Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                               refresh();
                           }
                       })
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                               // do nothing
                           }
                       })
                       .setIcon(android.R.drawable.ic_dialog_alert)
                       .show();
               return true;
           }
       });
    }

}
