package mabwe.com.mabwe.fcmServices;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by mindiii on 15/5/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(String token) {
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }
}
