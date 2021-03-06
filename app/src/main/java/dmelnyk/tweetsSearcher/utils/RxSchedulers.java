package dmelnyk.tweetsSearcher.utils;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by dmitry on 29.04.17.
 */

public class RxSchedulers {

    public Scheduler getMainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }

    public Scheduler getIoSchedulers() {
        return Schedulers.io();
    }
}
