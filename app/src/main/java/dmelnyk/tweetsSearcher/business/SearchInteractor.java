package dmelnyk.tweetsSearcher.business;

import android.support.annotation.NonNull;

import dmelnyk.tweetsSearcher.business.model.Tweet;
import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchTweetModel;
import io.reactivex.Observable;

/**
 * Created by dmitry on 01.05.17.
 */

public class SearchInteractor implements ISearchInteractor {

    @Override
    public Observable<Tweet> loadTweets(@NonNull CharSequence searchRequest) {
        return null;
    }

    private Observable<SearchTweetModel> getRawTweets(String searchRequest) {
        return null;
    }

    private Observable<Tweet> convertTweets(Observable<SearchTweetModel> observable) {
        return observable
                .map(rawTweet -> Tweet.Builder()
                        .withName(rawTweet.getUser().getUserName())
                        .withText(rawTweet.getTextTweet())
                        .withImageUrl(rawTweet.getUser().getUserImageUrl())
                        .withDate(rawTweet.getDateCreation())
                        .withLikes(rawTweet.getLikesCount())
                        .withRetweets(rawTweet.getRetweetCount())
                        .build()
                );
    }
}
