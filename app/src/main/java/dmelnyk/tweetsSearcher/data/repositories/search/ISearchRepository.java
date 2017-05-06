package dmelnyk.tweetsSearcher.data.repositories.search;

import java.io.IOException;

import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchTweetModel;
import io.reactivex.Observable;

/**
 * Created by dmitry on 03.05.17.
 */

public interface ISearchRepository {

    Observable<SearchTweetModel> getTweets(String request) throws IOException;
}
