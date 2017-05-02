package dmelnyk.tweetsSearcher.business;

import android.support.annotation.NonNull;

import dmelnyk.tweetsSearcher.business.model.Tweet;
import io.reactivex.Observable;

/**
 * Created by dmitry on 01.05.17.
 */

public interface ISearchInteractor {

    public Observable<Tweet> loadTweets(@NonNull CharSequence searchRequest);
}
