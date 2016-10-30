package app.jongyeop2.fireinthehouse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Han on 2016-10-26.
 */

public class FireHistoryActivity extends Activity {
    ArrayList<FireHistoryItem> data = new ArrayList<>();
    FireHistoryListviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_history);

        ListView listView = (ListView)findViewById(R.id.history_listview);

        adapter = new FireHistoryListviewAdapter(this, R.layout.fire_history_item, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);

        SharedPreferences historyFile = getSharedPreferences("history", 1);
        String historyStr = historyFile.getString("history", "");

        if(!historyStr.equals("")) {
            String[] itemArr = historyStr.split("__");
            for(int i = itemArr.length - 1; i >= 0 ; i--) {
                if(itemArr[i].equals("")) continue;

                String[] dataArr = itemArr[i].split("/");
                data.add(new FireHistoryItem(dataArr[0], dataArr[1], dataArr[2]));
            }
        }
        adapter.notifyDataSetChanged();
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            FireHistoryItem item = data.get(position);

            Intent intent = new Intent(FireHistoryActivity.this, FireActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("message", item.getMessage());
            intent.putExtra("date", item.getDate());
            startActivity(intent);
        }
    };
}
