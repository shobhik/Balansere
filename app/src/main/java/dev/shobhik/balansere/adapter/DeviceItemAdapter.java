package dev.shobhik.balansere.adapter;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.shobhik.balansere.R;

/**
 * Created by Shobhik Ghosh on 11/21/2017.
 */

public class DeviceItemAdapter extends ArrayAdapter {

    private final Context mAdapterContext;
    private List<WifiP2pDevice> originalData;
    private List<WifiP2pDevice> filteredData;
    private List<WifiP2pDevice> filteredVisible;
    private int selectedview = 0;

    public DeviceItemAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.mAdapterContext = context;
    }

    public DeviceItemAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.mAdapterContext = context;
        this.originalData = objects;
        this.filteredData = objects;
        this.filteredVisible = objects;
    }

    public List<WifiP2pDevice> getOriginalData() {
        return originalData;
    }

    public void setOriginalData(List<WifiP2pDevice> originalData) {
        this.originalData = originalData;
    }

    public List<WifiP2pDevice> getFilteredData() {
        return filteredData;
    }

    public void setFilteredData(List<WifiP2pDevice> filteredData) {
        this.filteredData = filteredData;
        filteredVisible.clear();
    }

    //For this helper method, return based on filteredData
    @Override
    public int getCount() {
        return filteredData.size();

    }

    //This should return a data object, not an int
    @Override
    public WifiP2pDevice getItem(int position){
        return filteredData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence){
                FilterResults results = new FilterResults();
                if(charSequence == null || charSequence.length() == 0){
                    results.values = originalData;
                    results.count = originalData.size();
                }else{
                    List<WifiP2pDevice> filterResultsData = new ArrayList<WifiP2pDevice>();
                    List<WifiP2pDevice> filterResultsVisible = new ArrayList<WifiP2pDevice>();

                    for(WifiP2pDevice searchWifiP2pDevice : originalData)  {
                        String name = searchWifiP2pDevice.deviceName;
                        String grouping = searchWifiP2pDevice.deviceAddress;
                        String data = name + grouping;
                        if(data.contains(charSequence) || data.toLowerCase().contains(charSequence)) {
                            filterResultsData.add(searchWifiP2pDevice);
                            filterResultsVisible.add(searchWifiP2pDevice);
                        }
                    }

                    results.values = filterResultsVisible;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults){
                filteredData = (List<WifiP2pDevice>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.completed_breakdown_item, null);
        LinearLayout countbox=(LinearLayout)convertView.findViewById(R.id.field_count);
        TextView countnum=(TextView)convertView.findViewById(R.id.field_count_num);
        TextView tvAttribNameView=(TextView)convertView.findViewById(R.id.field_name);
        TextView tvAttribGroupingView=(TextView)convertView.findViewById(R.id.field_sub_name);
        countbox.setVisibility(View.GONE);
        tvAttribGroupingView.setVisibility(View.GONE);
        tvAttribGroupingView.setVisibility(View.VISIBLE);

        WifiP2pDevice thisAssetAttribute = filteredData.get(position);
        String name = thisAssetAttribute.deviceName;
        String grouping = thisAssetAttribute.deviceAddress;

        tvAttribNameView.setText(name);
        tvAttribGroupingView.setVisibility(View.VISIBLE);
        tvAttribGroupingView.setText(grouping);
        countnum.setText("\u2713");
        tvAttribNameView.setTag("1");
        countbox.setTag("2");
        //countnum.setTag("3");
        convertView.setTag(selectedview);
        return convertView;
    }


    public void setSelectedView(int spec) {
        selectedview = spec;
    }



}
