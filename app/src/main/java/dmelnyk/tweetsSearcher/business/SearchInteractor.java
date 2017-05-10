package dmelnyk.tweetsSearcher.business;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dmelnyk.tweetsSearcher.business.model.EmptyTweet;
import dmelnyk.tweetsSearcher.business.model.Tweet;
import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchErrorTweetModel;
import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchTweetModel;
import dmelnyk.tweetsSearcher.data.repositories.search.ISearchRepository;
import io.reactivex.Observable;

/**
 * Created by dmitry on 01.05.17.
 */

public class SearchInteractor implements ISearchInteractor {

    private final ISearchRepository iSearchRepository;

    public SearchInteractor(ISearchRepository iSearchRepository) {
        this.iSearchRepository = iSearchRepository;
    }

    @Override
    public Observable<Tweet> loadTweets(@NonNull CharSequence searchRequest) throws IOException {

        Observable<SearchTweetModel> rawTweets = getRawTweets(searchRequest.toString());
        if (rawTweets.isEmpty().blockingGet()) {

            // NO_TWEETS_CODE
            return Observable.just(new EmptyTweet(1));
        }

        return convertTweets(rawTweets);
    }

    protected Observable<SearchTweetModel> getRawTweets(String searchRequest) throws IOException {

        return iSearchRepository.getTweets(searchRequest);    }

    public Observable<Tweet> convertTweets(Observable<SearchTweetModel> observable) {
        return observable
                .map(rawTweet -> {
                            if (rawTweet instanceof SearchErrorTweetModel) {
                                // NO_INTERNET_CONNECTION_CODE
                                return new EmptyTweet(2);
                            }

                            return Tweet.Builder()
                                    .withName(rawTweet.getUser().getUserName())
                                    .withText(rawTweet.getTextTweet())
                                    .withImageUrl(changeImageUrlAddress(rawTweet.getUser().getUserImageUrl()))
                                    .withDate(convertDate(rawTweet.getDateCreation()))
                                    .withLikes(rawTweet.getLikesCount())
                                    .withRetweets(rawTweet.getRetweetCount())
                                    .build();
                        }

                );
    }

    private String changeImageUrlAddress(String imageUrl) {
        // xxx_small.jpg, xxx_normal.jpg, xxx_big.jpg, => different size of image.
        int _lastIndex = imageUrl.lastIndexOf('_');
        // original size of avatar image

        String originalImageUrl = _lastIndex != -1
                ? imageUrl.substring(0, _lastIndex) + ".jpg"
                : imageUrl;

        return originalImageUrl;
    }

    private String convertDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM, hh:mm");
        return sdf.format(date);
    }
}
