package app.jongyeop2.fireinthehouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getIntent().getExtras() != null) {
            Intent intent = new Intent(MainActivity.this, FireActivity.class);
            intent.putExtra("title", getIntent().getStringExtra("title"));
            intent.putExtra("message", getIntent().getStringExtra("message"));
            intent.putExtra("date", getIntent().getStringExtra("date"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences tokenFile = getSharedPreferences("token", 0);

            if(tokenFile.getString("token", "").equals("")) {
                String token = FirebaseInstanceId.getInstance().getToken();

                try {
                    new PushDataAsyncTask().execute(token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClick(View view) {
        Intent intent = new Intent();

        switch(view.getId()) {
            case R.id.btn_setting:
                intent = new Intent(this, LoginActivity.class);
                break;

            case R.id.btn_push_list:
                intent = new Intent(this, PushListActivity.class);
                break;

            case R.id.btn_history:
                intent = new Intent(this, FireHistoryActivity.class);
                break;

            case R.id.btn_reset_app_data:
                SharedPreferences tokenFile = getSharedPreferences("token", 0);
                Toast.makeText(getApplicationContext(), "Token: " + tokenFile.getString("token", "") + ", save: " + tokenFile.getBoolean("save", false), Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = tokenFile.edit();
                editor.clear();
                editor.commit();
                return;
        }

        startActivity(intent);
    }

    private class PushDataAsyncTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog asyncDialog;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncDialog = new ProgressDialog(MainActivity.this);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("데이터 로딩중...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            asyncDialog.dismiss();

            if(aBoolean) {
                SharedPreferences tokenFile = getSharedPreferences("token", 0);
                SharedPreferences.Editor editor = tokenFile.edit();
                editor.putString("token", token);
                editor.commit();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            token = params[0];

            String url = "https://morning-depths-56555.herokuapp.com/";
            try {
                // CREATE USER PUSH DATA
                URL myPage = new URL(url + "api/user_push_data/");
                HttpURLConnection httpCon = (HttpURLConnection)myPage.openConnection();
                httpCon.setRequestMethod("POST");

                httpCon.setDoOutput(true);
                httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream out = httpCon.getOutputStream();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token", token);
                jsonObject.put("push_data", new JSONArray());

                out.write(jsonObject.toString().getBytes("UTF-8"));
                out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
                String data = "", line;
                while((line = in.readLine()) != null)
                    data += line;
                in.close();

                return data.contains("1");
            } catch (Exception e) { e.printStackTrace(); }

            return false;
        }
    }
}
