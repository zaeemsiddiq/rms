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
import au.edu.monash.rms.data.Constants;
import au.edu.monash.rms.data.Stop;

/**
 * Created by Zaeem on 6/1/2016.
 */
public class StopsListAdapter extends ArrayAdapter implements Filterable {
    List<Stop> stopList;
    List<Stop> origStopList;
    private Context context;
    private SearchFilter searchFilter;

    private class ViewHolder {
        TextView StopID;
        TextView StopName;
    }
    public StopsListAdapter(Context context, List<Stop> list) {
        super(context, android.R.layout.simple_dropdown_item_1line, list);
        this.stopList = Constants.stopList;
        this.origStopList = Constants.stopList;
        this.context = context;
    }

    public Stop getItem(int position)
    {
        return stopList.get(position);
    }

    public int getCount() { return stopList.size(); }

    public void resetData() {   // calling this method to reset the filterable data to original list which was initialised at start
        stopList = origStopList;
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
            v = inflater.inflate(R.layout.list_stop_item, null);
            TextView NodeID = (TextView) v.findViewById(R.id.textViewStopID);
            TextView NodeName = (TextView) v.findViewById(R.id.textViewStopName);
            viewHolder.StopID = NodeID;
            viewHolder.StopName = NodeName;
            v.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) v.getTag();

        Stop d = getItem(position);
        viewHolder.StopID.setText(String.valueOf(d.StopID));
        viewHolder.StopName.setText(d.StopName);
        return v;
    }

    private class SearchFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = stopList;
                results.count = stopList.size();
            }
            else {
                List<Stop> filteredList = new ArrayList<Stop>();
                for (Stop c : stopList) {
                    if (c.StopName.toUpperCase().contains(constraint.toString().toUpperCase()))
                        filteredList.add(c);
                }
                results.values = filteredList;
                results.count = filteredList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            stopList = (List<Stop>) results.values;
            notifyDataSetChanged();
        }

    }
}
