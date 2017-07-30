package rsdhupia.ca.roadsguide;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 07/11/2015.
 */
public class CustomAdapter extends BaseAdapter{


    private static ArrayList<Restriction> list;

    private LayoutInflater mInflater;

    public CustomAdapter(Context context, ArrayList<Restriction> results) {
        Log.d("CLASS", "CustomAdapter");
        list = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_row, null);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.roadName);
            holder.txtZone = (TextView) convertView.findViewById(R.id.zoneName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(list.get(position).getRoadAffected());
        Log.d("CustomAdapter", list.get(position).getRoadAffected());
        holder.txtZone.setText(list.get(position).getWorkZone());
        Log.d("CustomAdapter", list.get(position).getWorkZone());


        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtZone;
    }
}
