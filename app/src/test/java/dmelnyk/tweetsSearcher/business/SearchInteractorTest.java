package dmelnyk.tweetsSearcher.business;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dmelnyk.tweetsSearcher.business.model.Tweet;
import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchTweetModel;
import dmelnyk.tweetsSearcher.data.repositories.search.SearchRepository;
import dmelnyk.tweetsSearcher.data.repositories.search.core.NetworkUtil;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by dmitry on 01.05.17.
 */

public class SearchInteractorTest {

    Observable<SearchTweetModel> observableTweetModel;

    String tweetText = "Softwareentwickler Schwerpunkt Android in Hamburg, #Softwareentwicklung #IBM #Android #RxJava";
    String tweetDate = "Fri Apr 28 00:16:41 +0000 2017";
    String userName = "Vasia Pupkin";
    String usrImageUrl = "image url";
    int tweetLikes = 5;
    int tweetRetweets = 2;

    @Before
    public void createFakeTweetModel() {
        SearchTweetModel tweetModel = new SearchTweetModel(tweetText, null, tweetLikes, tweetRetweets);

        SearchTweetModel.User user = new SearchTweetModel.User(userName, usrImageUrl);
        tweetModel.setUser(user);

        observableTweetModel = Observable.just(tweetModel);
    }

    @Mock
    Context context;

    @Test public void privateConvertTweets() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given:
        TestObserver<Tweet> testObserver = new TestObserver<>();
        SearchInteractor interactor = new SearchInteractor(new SearchRepository(new NetworkUtil(context)));

        Method convertTweets = SearchInteractor.class.getDeclaredMethod("convertTweets", Observable.class);
        convertTweets.setAccessible(true);

        // when:
        Observable<Tweet> tweetObservable = (Observable<Tweet>) convertTweets.invoke(interactor, observableTweetModel);
        tweetObservable.subscribe(testObserver);
        // then:
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        assertThat(testObserver.values().get(0).getUserName(), equalTo(userName));
        assertThat(testObserver.values().get(0).getTweetText(), equalTo(tweetText));
        assertThat(testObserver.values().get(0).getUserImageUrl(), equalTo(usrImageUrl));
        assertThat(testObserver.values().get(0).getLikes(), equalTo(tweetLikes));
        assertThat(testObserver.values().get(0).getRetweets(), equalTo(tweetRetweets));
        assertThat(testObserver.values().get(0).getTweetDate(), equalTo(tweetDate));
    }

}