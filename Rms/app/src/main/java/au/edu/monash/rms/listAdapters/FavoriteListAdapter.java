package au.edu.monash.rms.listAdapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.edu.monash.rms.R;
import au.edu.monash.rms.data.Favorite;

/**
 * Created by Zaeem on 6/1/2016.
 */
public class FavoriteListAdapter extends ArrayAdapter implements Filterable {
    List<Favorite> favorites;
    List<Favorite> origFavoritesList;
    private Context context;
    private SearchFilter searchFilter;

    private class ViewHolder {
        TextView FavoriteID;
        TextView FavoriteName;
        TextView FavoriteSubtext;
    }
    public FavoriteListAdapter(Context context, List<Favorite> list) {
        super(context, android.R.layout.simple_dropdown_item_1line, list);
        this.favorites = list;
        this.origFavoritesList = list;
        this.context = context;
    }

    public Favorite getItem(int position)
    {
        return favorites.get(position);
    }

    public int getCount() {
        if(favorites == null)
          return 0;
        return favorites.size();
    }

    public void resetData() {   // calling this method to reset the filterable data to original list which was initialised at start
        favorites = origFavoritesList;
    }

    @Override
    public Filter getFilter() {
        if (searchFilter == null)
            searchFilter = new SearchFilter();
        return searchFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_favorite_item, null);
            TextView NodeID = (TextView) v.findViewById(R.id.textViewFavoriteID);
            TextView NodeName = (TextView) v.findViewById(R.id.textViewFavoriteName);
            TextView NodeSubtext = (TextView) v.findViewById(R.id.favoritesSubtext);

            viewHolder.FavoriteID = NodeID;
            viewHolder.FavoriteName = NodeName;
            viewHolder.FavoriteSubtext = NodeSubtext;
            v.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) v.getTag();

        Favorite d = getItem(position);
        viewHolder.FavoriteID.setText(String.valueOf(d.getFavouriteID()));
        viewHolder.FavoriteName.setText(d.getFavoriteName());
        viewHolder.FavoriteSubtext.setText(d.getSourceStop().getStopName()+" - "+d.getDestinationStop().getStopName());
        return v;
    }

    private class SearchFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = favorites;
                results.count = favorites.size();
            }
            else {
                List<Favorite> filteredList = new ArrayList<Favorite>();
                for (Favorite c : favorites) {
                    if (c.FavoriteName.toUpperCase().contains(constraint.toString().toUpperCase()))
                        filteredList.add(c);
                }
                results.values = filteredList;
                results.count = filteredList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            favorites = (List<Favorite>) results.values;
            notifyDataSetChanged();
        }

    }
}
