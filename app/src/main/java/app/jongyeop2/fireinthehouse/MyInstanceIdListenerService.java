package app.jongyeop2.fireinthehouse;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Han on 2016-10-24.
 */

public class MyInstanceIdListenerService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.v("log", "Change My Id!!!!!!" + refreshedToken);

        /*
        // 바뀐 토큰(혹은 처음 받은 토큰)에 대한 처리
        SharedPreferences tokenFile = getSharedPreferences("token", 0);
        if(tokenFile.getString("token", "").equals("")) {
            SharedPreferences.Editor editor = tokenFile.edit();
            editor.putString("token", refreshedToken);
            editor.commit();
        } else {
            // 바뀐 토큰에 대한 적절한 처리
        }
        */

    }
}
