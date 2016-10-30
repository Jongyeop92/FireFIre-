package app.jongyeop2.fireinthehouse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Han on 2016-10-24.
 */

public class RegisterActivity extends Activity {
    AlertDialog.Builder ok_alert_builder, yes_no_alert_builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ok_alert_builder = new AlertDialog.Builder(this);
        ok_alert_builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        yes_no_alert_builder = new AlertDialog.Builder(this);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_register_send:
                JSONObject jsonObject = new JSONObject();

                EditText editRegisterId = (EditText)findViewById(R.id.edit_register_id);
                EditText editRegisterPassword = (EditText)findViewById(R.id.edit_register_password);
                EditText editRegisterSerialNumber = (EditText)findViewById(R.id.edit_register_serial_number);
                EditText editRegisterAddress = (EditText)findViewById(R.id.edit_register_address);
                EditText editRegisterAdditionalInfo = (EditText)findViewById(R.id.edit_register_additional_info);

                try {
                    jsonObject.put("serial_number", editRegisterSerialNumber.getText().toString());
                    jsonObject.put("user_id", editRegisterId.getText().toString());
                    jsonObject.put("password", editRegisterPassword.getText().toString());
                    jsonObject.put("address", editRegisterAddress.getText().toString());
                    jsonObject.put("additional_info", editRegisterAdditionalInfo.getText().toString());
                    jsonObject.put("sms_number_list", new JSONArray());

                    new RegisterAsyncTask().execute(jsonObject);
                } catch (Exception e) { e.printStackTrace(); }
                break;

            case R.id.btn_register_cancel:
                yes_no_alert_builder.setMessage("등록을 취소 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
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

    private class RegisterAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {
        ProgressDialog asyncDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncDialog = new ProgressDialog(RegisterActivity.this);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("데이터 등록중...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            asyncDialog.dismiss();

            if(aBoolean) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("register", true);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "기기 정보 등록 실패!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            String url = "https://morning-depths-56555.herokuapp.com/";

            JSONObject jsonObject = params[0];
            try {
                // CREATE USER
                URL myPage = new URL(url + "api/users/");
                HttpURLConnection httpCon = (HttpURLConnection)myPage.openConnection();
                httpCon.setRequestMethod("POST");

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

                // CREATE PUSH ALARM TOKEN LIST
                myPage = new URL(url + "api/tokenLists/");
                httpCon = (HttpURLConnection)myPage.openConnection();
                httpCon.setRequestMethod("POST");

                httpCon.setDoOutput(true);
                httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                out = httpCon.getOutputStream();

                JSONObject newJsonObject = new JSONObject();
                newJsonObject.put("serial_number", jsonObject.getString("serial_number"));
                newJsonObject.put("token_list", new JSONArray());

                out.write(newJsonObject.toString().getBytes("UTF-8"));
                out.close();

                in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
                String data2 = "";
                while((line = in.readLine()) != null)
                    data2 += line;
                in.close();

                return data.contains("1") && data2.contains("1");
            } catch (Exception e) { e.printStackTrace(); }

            return false;
        }
    }
}

