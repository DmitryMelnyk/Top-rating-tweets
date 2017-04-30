package dmelnyk.tweetsSearcher.application;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by dmitry on 29.04.17.
 */

public class MyApp extends Application {

    // Dagger2 AppComponent
    @NonNull
    private AppComponent appComponent;

    @NonNull
    public AppComponent getAppComponent() {
        return appComponent;
    }

    @NonNull
    public static MyApp get(@NonNull Context context) {
        return (MyApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
