package app.jongyeop2.fireinthehouse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class SettingActivity extends Activity {
    ArrayList<SmsItem> data = new ArrayList<>();
    SmsListviewAdapter adapter;

    AlertDialog.Builder ok_alert_builder, yes_no_alert_builder, remove_alert_builder, add_alert_builder;

    EditText inputPhoneNumber;
    LinearLayout mainLayout;
    SmsItem smsItem = null;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_id");

        JSONObject settingData = new JSONObject();

        try {
            new GetSettingAsyncTask().execute(user_id);
        } catch (Exception e) { e.printStackTrace(); }

        ok_alert_builder = new AlertDialog.Builder(this);
        ok_alert_builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        yes_no_alert_builder = new AlertDialog.Builder(this);
        remove_alert_builder = new AlertDialog.Builder(this);
        add_alert_builder = new AlertDialog.Builder(this);

        ListView listView = (ListView)findViewById(R.id.listview_sms);

        adapter = new SmsListviewAdapter(this, R.layout.sms_list_item, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);


        inputPhoneNumber = new EditText(this);

        mainLayout = new LinearLayout(getApplicationContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout nameLayout = new LinearLayout(getApplicationContext());
        TextView textPhoneNumber = new TextView(getApplicationContext());
        textPhoneNumber.setText("전화번호");
        textPhoneNumber.setWidth(300);

        nameLayout.addView(textPhoneNumber);
        nameLayout.addView(inputPhoneNumber);

        mainLayout.addView(nameLayout);

        add_alert_builder.setMessage("전화 번호를 입력하세요").setView(mainLayout);
        add_alert_builder.setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // YES
                smsItem = new SmsItem(inputPhoneNumber.getText().toString());
                inputPhoneNumber.setText("");
                data.add(smsItem);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "항목 추가", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // NO
            }
        });
        alert = add_alert_builder.create();

        Button btnAddPhoneNumber = (Button)findViewById(R.id.btn_add_phone_number);
        btnAddPhoneNumber.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_modify_send:
                EditText editModifyAddress = (EditText)findViewById(R.id.edit_modify_address);
                EditText editModifyAdditionalInfo = (EditText)findViewById(R.id.edit_modify_additional_info);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("address", editModifyAddress.getText().toString());
                    jsonObject.put("additional_info", editModifyAdditionalInfo.getText().toString());
                    jsonObject.put("user_id", getIntent().getStringExtra("user_id"));

                    JSONArray new_sms_number_list = new JSONArray();

                    for(int i = 0; i < data.size(); i++)
                        new_sms_number_list.put(data.get(i).getPhoneNumber());

                    jsonObject.put("sms_number_list", new_sms_number_list);
                } catch (Exception e) { e.printStackTrace(); }

                try {
                    new SetSettingAsyncTask().execute(jsonObject);
                } catch (Exception e) { e.printStackTrace(); }
                break;

            case R.id.btn_modify_cancel:
                yes_no_alert_builder.setMessage("수정을 취소 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // YES
                        finish();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // NO
                    }
                });
                AlertDialog alert = yes_no_alert_builder.create();
                alert.show();
                break;
        }
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            remove_alert_builder.setMessage("항목을 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // YES
                    data.remove(position);
                    adapter.notifyDataSetChanged();
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

    public void showSettingData(JSONObject settingData) {
        TextView textSerialNumber = (TextView)findViewById(R.id.text_serial_number);
        EditText editModifyAddress = (EditText)findViewById(R.id.edit_modify_address);
        EditText editModifyAdditionalInfo = (EditText)findViewById(R.id.edit_modify_additional_info);

        try {
            textSerialNumber.setText(settingData.getString("serial_number"));
            editModifyAddress.setText(settingData.getString("address"));
            editModifyAdditionalInfo.setText(settingData.getString("additional_info"));
        } catch (Exception e) { e.printStackTrace(); }

        try {
            JSONArray sms_number_list = settingData.getJSONArray("sms_number_list");

            for(int i = 0; i < sms_number_list.length(); i++)
                data.add(new SmsItem(sms_number_list.getString(i)));
        } catch (Exception e) { e.printStackTrace(); }
        adapter.notifyDataSetChanged();
    }

    private class GetSettingAsyncTask extends AsyncTask<String, Void , JSONObject> {

        ProgressDialog asyncDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncDialog = new ProgressDialog(SettingActivity.this);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("데이터 로딩중...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            asyncDialog.dismiss();
            SettingActivity.this.showSettingData(jsonObject);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = "https://morning-depths-56555.herokuapp.com/";

            try {
                URL myPage = new URL(url + "api/users/" + params[0]);
                BufferedReader in = new BufferedReader(new InputStreamReader(myPage.openStream()));

                String data = "", line;
                while((line = in.readLine()) != null)
                    data += line;
                in.close();

                JSONObject jsonObject;

                if(data.indexOf('[') == 0) {
                    jsonObject = new JSONArray(data).getJSONObject(0);
                }
                else {
                    jsonObject = new JSONObject(data);
                }

                return jsonObject;
            } catch (Exception e) { e.printStackTrace(); }

            return new JSONObject();
        }
    }

    private class SetSettingAsyncTask extends  AsyncTask<JSONObject, Void, Boolean> {

        ProgressDialog asyncDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncDialog = new ProgressDialog(SettingActivity.this);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("데이터 수정중...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean isSet) {
            asyncDialog.dismiss();

            if(isSet) {
                ok_alert_builder.setMessage("정보 수정 완료");
            }
            else {
                ok_alert_builder.setMessage("정보 수정 실패");
            }
            ok_alert_builder.show();
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            String url = "https://morning-depths-56555.herokuapp.com/";

            JSONObject jsonObject = params[0];
            try {
                String user_id = jsonObject.getString("user_id");
                jsonObject.remove(user_id);

                URL myPage = new URL(url + "api/users/" + user_id);
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

                return data.contains("updated");
            } catch (Exception e) { e.printStackTrace(); }

            return false;
        }
    }
}

