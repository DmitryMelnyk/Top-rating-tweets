package dmelnyk.tweetsSearcher.data.repositories.search.core;

import dmelnyk.tweetsSearcher.data.network.models.response.search.raw.TweetRawObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by dmitry on 03.05.17.
 */

public interface TwitterApi {

    String BASE_URL = "https://api.twitter.com/";

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<OAthToken> getToken(@Field("grant_type") String grantType);

    // getting tweets
    @GET("1.1/search/tweets.json")
    Call<TweetRawObject> searchTweets(
            @Query("q") String request,
            @Query("count") int count);
}
