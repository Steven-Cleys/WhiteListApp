package com.ap.steven.digipolis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 3/5/2015.
 *
 */
public class ListAdapter extends BaseAdapter {

    NetworkHandler handler = NetworkHandler.getNetworkHandler();
    List<Contact> fullContactlist = handler.loadData();
    List<Contact> contactlist = fullContactlist;


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return contactlist.size();
    }

    @Override
    public Contact getItem(int pos) {
        // TODO Auto-generated method stub
        return contactlist.get(pos);
    }

    @Override
    public long getItemId(int id) {
        // TODO Auto-generated method stub
        return id;
    }

/*    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(MyApplication.getAppContext()); //  (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item,parent, false);
        }

        TextView Name = (TextView) convertView.findViewById(R.id.textView1);
        TextView Number = (TextView) convertView.findViewById(R.id.textView2);

        Contact contactje = contactlist.get(position);

        Name.setText(contactje.name);
        Number.setText(contactje.tel);

        return convertView;
    }

    public void filter(CharSequence charSequence) {
        if (charSequence != null) {
            ArrayList<Contact> filterResultsData = new ArrayList<Contact>();
            String filterString = charSequence.toString().toLowerCase();
            String filterableString;

            for (Contact c : fullContactlist) {
                filterableString = c.toString();
                if (filterableString.toLowerCase().contains(filterString)) {
                    filterResultsData.add(c);
                }
            }

            contactlist = filterResultsData;
            notifyDataSetChanged();
        }
    }

    public void refresh() {
        notifyDataSetChanged();
    }


}