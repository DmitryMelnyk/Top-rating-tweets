package dmelnyk.tweetsSearcher.data.repositories.search;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchErrorTweetModel;
import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchTweetModel;
import dmelnyk.tweetsSearcher.data.network.models.response.search.raw.TweetRawObject;
import dmelnyk.tweetsSearcher.data.repositories.search.core.NetworkUtil;
import dmelnyk.tweetsSearcher.data.repositories.search.core.OAthToken;
import dmelnyk.tweetsSearcher.data.repositories.search.core.TwitterApi;
import io.reactivex.Observable;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dmitry on 03.05.17.
 */

public class SearchRepository implements ISearchRepository {

    private final String grantType = "client_credentials";
    private final int numTweets = 500;

    private final String TAG = SearchRepository.class.getSimpleName();
    private final NetworkUtil networkUtil;
    private OAthToken token;
    private String credentials = Credentials.basic(
            "daZKq6Y7WueePhfhovB3mYO3W", // app key
            "H16bwXtn525OPQJGX4bjni583MJFaG2g1I1IZvSRdQ7l8UBqda"); // app secret

    public SearchRepository(NetworkUtil networkUtil) {
        this.networkUtil = networkUtil;
    }

    @Override
    public Observable<SearchTweetModel> getTweets(String request) throws IOException {
        // checking network connection
        if (!networkUtil.isNetworkReachable()) {
            Log.d(TAG, "Network is not reachable. Please, check connection!");
            return Observable.error(() ->
                    new SearchErrorTweetModel("Network is not reachable. Please, check connection!", -1));
        }

        if (token == null) {
            Response<OAthToken> response = twitterApi.getToken(grantType).execute();
            Log.d(TAG, String.format("response code = %d, message = %s ", response.code(), response.message()));

            if (response.code() == 200) {
                initializeToken(response.body());
                Log.d(TAG, token.getAuthorization());
            } else {
                return Observable.error(() ->
                        new SearchErrorTweetModel(response.message(), response.code()));
            }
        }

        return getTweetsModel(request);
    }

    private Observable<SearchTweetModel> getTweetsModel(String request) throws IOException {
        Response<TweetRawObject> tweetRawObjectResponse =
                twitterApi.searchTweets (request, numTweets).execute();

        Log.d(TAG, String.format("response code = %d, message = %s error = %s",
                tweetRawObjectResponse.code(), tweetRawObjectResponse.message(),
                tweetRawObjectResponse.errorBody()
                ));

        List<SearchTweetModel> tweets = tweetRawObjectResponse.body().getTweets();

        return Observable.fromIterable(tweets);
    }

    private OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request original = chain.request();
                Request newRequest = original.newBuilder()
                        .addHeader("Authorization", token != null
                                ? token.getAuthorization()
                                : credentials)
                        .build();
                return chain.proceed(newRequest);
            }).build();

    private Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
        final DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return df.parse(json.getAsString());
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }).create();

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(TwitterApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build();

    private TwitterApi twitterApi = retrofit.create(TwitterApi.class);

    private void initializeToken(OAthToken token) {
        this.token = token;
    }
}
