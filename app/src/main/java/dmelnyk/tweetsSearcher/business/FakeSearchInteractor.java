package dmelnyk.tweetsSearcher.business;

import android.support.annotation.NonNull;

import dmelnyk.tweetsSearcher.business.model.Tweet;
import io.reactivex.Observable;

/**
 * Created by dmitry on 01.05.17.
 */

public class FakeSearchInteractor implements ISearchInteractor {

    @Override
    public Observable<Tweet> loadTweets(@NonNull CharSequence searchRequest) {

        Tweet tweet = Tweet.Builder()
                .withText("Softwareentwickler Schwerpunkt Android in Hamburg, #Softwareentwicklung #IBM #Android #RxJava")
                .withName("John Doe")
                .withImageUrl("url")
                .withDate("Fri Apr 28 00:16:41 +0000 2017")
                .withLikes(12)
                .withRetweets(3)
                .build();

        return Observable.just(tweet, tweet, tweet, tweet, tweet);
    }

}
