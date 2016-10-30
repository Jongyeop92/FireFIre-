package app.jongyeop2.fireinthehouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Han on 2016-10-24.
 */

public class SmsListviewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<SmsItem> data;
    private int layout;

    public SmsListviewAdapter(Context context, int layout, ArrayList<SmsItem> data) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        SmsItem item = data.get(position);

        //TextView nameText = (TextView)convertView.findViewById(R.id.text_item_receiver_name);
        //nameText.setText(item.getName());

        TextView phoneNumberText = (TextView)convertView.findViewById(R.id.text_item_phone_number);
        phoneNumberText.setText(item.getPhoneNumber());

        return convertView;
    }
}

