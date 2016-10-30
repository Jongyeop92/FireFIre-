package app.jongyeop2.fireinthehouse;

import android.app.Activity;
import android.app.ProgressDialog;
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
import java.net.URL;

/**
 * Created by Han on 2016-10-24.
 */

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((EditText)findViewById(R.id.edit_id)).setText("");
        ((EditText)findViewById(R.id.edit_password)).setText("");

        if(getIntent().getBooleanExtra("register", false)) {
            Toast.makeText(getApplicationContext(), "기기 정보 등록 성공!\n로그인 해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View view) {
        Intent intent;

        switch(view.getId()) {
            case R.id.btn_login:
                EditText editId = (EditText)findViewById(R.id.edit_id);
                EditText editPassword = (EditText)findViewById(R.id.edit_password);

                String user_id = editId.getText().toString();
                String user_password = editPassword.getText().toString();

                editId.setText("");
                editPassword.setText("");

                try {
                    new LoginCheckAsyncTask().execute(user_id, user_password);
                } catch (Exception e) { e.printStackTrace(); }
                break;

            case R.id.btn_register:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class LoginCheckAsyncTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog asyncDialog;
        String user_id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncDialog = new ProgressDialog(LoginActivity.this);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로그인 중...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            asyncDialog.dismiss();

            if (aBoolean) {
                Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected Boolean doInBackground(String... params) {
            user_id = params[0];
            String user_password = params[1];

            String url = "https://morning-depths-56555.herokuapp.com/";

            try {
                URL myPage = new URL(url + "api/users/" + user_id);
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

                String password = jsonObject.getString("password");

                return password != null && password.equals(user_password);
            } catch (Exception e) { e.printStackTrace(); }

            return false;
        }
    }
}

