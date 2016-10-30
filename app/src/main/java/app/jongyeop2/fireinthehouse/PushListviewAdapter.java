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

public class PushListviewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<PushItem> data;
    private int layout;

    public PushListviewAdapter(Context context, int layout, ArrayList<PushItem> data) {
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

        PushItem item = data.get(position);

        TextView nameText = (TextView)convertView.findViewById(R.id.text_item_name);
        nameText.setText(item.getName());

        TextView tokenText = (TextView)convertView.findViewById(R.id.text_item_serial_number);
        tokenText.setText(item.getSerialNumber());

        return convertView;
    }
}
