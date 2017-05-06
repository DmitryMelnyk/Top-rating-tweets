package dmelnyk.tweetsSearcher.data.repositories.search.core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by dmitry on 03.05.17.
 */
public class NetworkUtil {
    private final Context context;

    public NetworkUtil(Context context) {
        this.context = context;
    }

    public boolean isNetworkReachable() {
        final ConnectivityManager cManager =
                (ConnectivityManager) context.getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo current = cManager.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.getState() == NetworkInfo.State.CONNECTED
                && ((current.getType() == cManager.TYPE_MOBILE
                || current.getType() == cManager.TYPE_WIFI)));
    }
}
