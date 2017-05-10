package dmelnyk.tweetsSearcher.data.repositories.search;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchErrorTweetModel;
import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchTweetModel;
import dmelnyk.tweetsSearcher.data.repositories.search.core.NetworkUtil;
import dmelnyk.tweetsSearcher.data.repositories.search.core.OAthToken;
import dmelnyk.tweetsSearcher.data.repositories.search.core.TwitterApi;
import dmelnyk.tweetsSearcher.ui.search.SearchActivity;

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
            return Observable.just(new SearchErrorTweetModel("Network is not reachable. Please, check connection!", SearchActivity.TOAST_CODE_NO_INTERNET_CONNECTION));

        }

        if (token == null) {
            Response<OAthToken> response = twitterApi.getToken(grantType).execute();
            Log.d(TAG, String.format("response code = %d, message = %s ", response.code(), response.message()));

            if (response.code() == 200) {
                initializeToken(response.body());
                Log.d(TAG, token.getAuthorization());
            } else {
                return Observable.just(new SearchErrorTweetModel(response.message(), response.code()));

            }
        }

        return getTweetsModel(request);
    }

    private Observable<SearchTweetModel> getTweetsModel(String request) throws IOException {
        return twitterApi.searchTweets(request, numTweets)
                .flatMapObservable(rawObject -> Observable.fromIterable(rawObject.getTweets()));

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
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    private TwitterApi twitterApi = retrofit.create(TwitterApi.class);

    private void initializeToken(OAthToken token) {
        this.token = token;
    }
}
