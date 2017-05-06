package dmelnyk.tweetsSearcher.data.network.models.response.search.raw;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchTweetModel;

/**
 * Created by dmitry on 03.05.17.
 */

public class TweetRawObject {

    @SerializedName("statuses")
    private List<SearchTweetModel> tweets;

    @SerializedName("search_metadata")
    private MetaData metaData;

    public MetaData getMetaData() {
        return metaData;
    }

    public List<SearchTweetModel> getTweets() {
        return tweets;
    }
}
