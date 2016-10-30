package app.jongyeop2.fireinthehouse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Han on 2016-10-24.
 */

public class PushListActivity extends Activity {
    ArrayList<PushItem> data = new ArrayList<>();
    PushListviewAdapter adapter;

    AlertDialog.Builder remove_alert_builder, add_alert_builder;

    EditText inputName;
    EditText inputSerialNumber;
    LinearLayout mainLayout;
    PushItem pushItem = null;
    AlertDialog alert;

    int nowPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_list);

        remove_alert_builder = new AlertDialog.Builder(this);
        add_alert_builder = new AlertDialog.Builder(this);

        ListView listView = (ListView)findViewById(R.id.listview);

        try {
            new GetPushListAsyncTask().execute();
        } catch (Exception e) { e.printStackTrace(); }

        adapter = new PushListviewAdapter(this, R.layout.push_list_item, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);

        inputName = new EditText(this);
        inputSerialNumber = new EditText(this);

        mainLayout = new LinearLayout(getApplicationContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout nameLayout = new LinearLayout(getApplicationContext());
        TextView textName = new TextView(getApplicationContext());
        textName.setText("이름");
        textName.setWidth(300);

        nameLayout.addView(textName);
        nameLayout.addView(inputName);

        LinearLayout serialLayout = new LinearLayout(getApplicationContext());
        TextView textSerial = new TextView(getApplicationContext());
        textSerial.setText("시리얼 번호");
        textSerial.setWidth(300);

        serialLayout.addView(textSerial);
        serialLayout.addView(inputSerialNumber);

        mainLayout.addView(nameLayout);
        mainLayout.addView(serialLayout);

        add_alert_builder.setMessage("이름과 시리얼 번호를 입력하세요").setView(mainLayout);
        add_alert_builder.setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // YES
                String name = inputName.getText().toString();
                String serialNumber = inputSerialNumber.getText().toString();

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("serial_number", serialNumber);
                    jsonObject.put("nickname", name);
                    new AddPushDataAsyncTask().execute(jsonObject);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // NO
            }
        });
        alert = add_alert_builder.create();

        Button btnAddItem = (Button)findViewById(R.id.btn_add_push_item);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });
    }

    public void showPushList(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("push_data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                PushItem item = new PushItem(jsonData.getString("nickname"), jsonData.getString("serial_number"));
                data.add(item);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void savePushData(Boolean isAdded) {
        if(isAdded) {
            String name = inputName.getText().toString();
            String serialNumber = inputSerialNumber.getText().toString();
            pushItem = new PushItem(name, serialNumber);
            inputName.setText("");
            inputSerialNumber.setText("");
            data.add(pushItem);
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "항목 추가", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "추가 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public void removePushData(Boolean isRemoved) {
        if(isRemoved) {
            data.remove(nowPosition);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
        }
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            remove_alert_builder.setMessage("항목을 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // YES
                    String serialNumber = data.get(position).getSerialNumber();
                    nowPosition = position;

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("serial_number", serialNumber);
                        new RemovePushDataAsyncTask().execute(jsonObject);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // NO
                }
            });
            AlertDialog alert = remove_alert_builder.create();
            alert.show();
        }
    };

    private class GetPushListAsyncTask extends AsyncTask<Void, Void , JSONObject> {

        ProgressDialog asyncDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncDialog = new ProgressDialog(PushListActivity.this);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("데이터 로딩중...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            asyncDialog.dismiss();
            PushListActivity.this.showPushList(jsonObject);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            String url = "https://morning-depths-56555.herokuapp.com/";

            try {
                SharedPreferences tokenFile = getSharedPreferences("token", 0);
                String token = tokenFile.getString("token", "");
                URL myPage = new URL(url + "api/user_push_data/" + token);
                BufferedReader in = new BufferedReader(new InputStreamReader(myPage.openStream()));

                String data = "", line;
                while((line = in.readLine()) != null)
                    data += line;
                in.close();

                JSONObject jsonObject;

                jsonObject = new JSONObject(data);

                return jsonObject;
            } catch (Exception e) { e.printStackTrace(); }

            return new JSONObject();
        }
    }

    private class AddPushDataAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {

        ProgressDialog asyncDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncDialog = new ProgressDialog(PushListActivity.this);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("데이터 저장중...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            asyncDialog.dismiss();
            PushListActivity.this.savePushData(aBoolean);
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            String url = "https://morning-depths-56555.herokuapp.com/";

            JSONObject jsonObject = params[0];

            try {
                SharedPreferences tokenFile = getSharedPreferences("token", 0);
                String token = tokenFile.getString("token", "");

                // Add USER PUSH DATA
                URL myPage = new URL(url + "api/user_push_data/add/" + token);
                HttpURLConnection httpCon = (HttpURLConnection)myPage.openConnection();
                httpCon.setRequestMethod("PUT");

                httpCon.setDoOutput(true);
                httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream out = httpCon.getOutputStream();

                out.write(jsonObject.toString().getBytes("UTF-8"));
                out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));

                String data = "", line;
                while((line = in.readLine()) != null)
                    data += line;
                in.close();


                // Add PUSH DATA IN PUSH LIST
                myPage = new URL(url + "api/tokenLists/add/" + jsonObject.getString("serial_number"));
                httpCon = (HttpURLConnection)myPage.openConnection();
                httpCon.setRequestMethod("PUT");

                httpCon.setDoOutput(true);
                httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                out = httpCon.getOutputStream();

                jsonObject.put("token", token);

                out.write(jsonObject.toString().getBytes("UTF-8"));
                out.close();

                in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));

                String data2 = "";
                while((line = in.readLine()) != null)
                    data2 += line;
                in.close();

                return data.contains("added") && data2.contains("added");
            } catch (Exception e) { e.printStackTrace(); }

            return false;
        }
    }

    private class RemovePushDataAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {

        ProgressDialog asyncDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncDialog = new ProgressDialog(PushListActivity.this);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("데이터 삭제중...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            asyncDialog.dismiss();
            PushListActivity.this.removePushData(aBoolean);
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            String url = "https://morning-depths-56555.herokuapp.com/";

            JSONObject jsonObject = params[0];

            try {
                SharedPreferences tokenFile = getSharedPreferences("token", 0);
                String token = tokenFile.getString("token", "");

                // Remove USER PUSH DATA
                URL myPage = new URL(url + "api/user_push_data/remove/" + token);
                HttpURLConnection httpCon = (HttpURLConnection)myPage.openConnection();
                httpCon.setRequestMethod("PUT");

                httpCon.setDoOutput(true);
                httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream out = httpCon.getOutputStream();

                out.write(jsonObject.toString().getBytes("UTF-8"));
                out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));

                String data = "", line;
                while((line = in.readLine()) != null)
                    data += line;
                in.close();

                Log.v("remove", "Remove USER PUSH DATA");


                // Remove PUSH DATA in PUSH LIST
                myPage = new URL(url + "api/tokenLists/remove/" + jsonObject.getString("serial_number"));
                httpCon = (HttpURLConnection)myPage.openConnection();
                httpCon.setRequestMethod("PUT");

                httpCon.setDoOutput(true);
                httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                out = httpCon.getOutputStream();

                jsonObject.put("token", token);

                out.write(jsonObject.toString().getBytes("UTF-8"));
                out.close();

                in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));

                String data2 = "";
                while((line = in.readLine()) != null)
                    data2 += line;
                in.close();

                Log.v("remove", "Remove PUSH DATA in PUSH LIST");

                return data.contains("removed") && data2.contains("removed");
            } catch (Exception e) { e.printStackTrace(); }

            return false;
        }
    }
}

