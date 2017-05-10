package dmelnyk.tweetsSearcher.business;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import dmelnyk.tweetsSearcher.business.model.EmptyTweet;
import dmelnyk.tweetsSearcher.business.model.Tweet;
import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchErrorTweetModel;
import dmelnyk.tweetsSearcher.data.network.models.response.search.SearchTweetModel;
import dmelnyk.tweetsSearcher.data.repositories.search.SearchRepository;
import dmelnyk.tweetsSearcher.data.repositories.search.core.NetworkUtil;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by dmitry on 01.05.17.
 */

@RunWith(MockitoJUnitRunner.class)
public class SearchInteractorTest {

    @Mock
    Context context;

    @Mock SearchRepository mockedRepository;

    Observable<SearchTweetModel> observableTweetModel;

    TestObserver<Tweet> testObserver;

    String tweetText = "Softwareentwickler Schwerpunkt Android in Hamburg, #Android #RxJava";
    String tweetDate = "Tue, 09 May, 11:23";
    String userName = "John Doe";
    String usrImageUrl = "imageurl.jpg";
    int tweetLikes = 5;
    int tweetRetweets = 2;
    private SearchInteractor interactor;

    @Before
    public void createFakeTweetModel() {
        Date date = new Date();
        date.setTime(1494361405067L);

        testObserver = new TestObserver<>();
        SearchTweetModel tweetModel = new SearchTweetModel(tweetText, date, tweetLikes, tweetRetweets);
        SearchTweetModel.User user = new SearchTweetModel.User(userName, usrImageUrl);
        tweetModel.setUser(user);

        observableTweetModel = Observable.just(tweetModel);

        interactor = new SearchInteractor(new SearchRepository(new NetworkUtil(context)));
    }

    @Test public void changeImageUrlAddress() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        Method changeImageUrl = SearchInteractor.class.getDeclaredMethod("changeImageUrlAddress", String.class);
        changeImageUrl.setAccessible(true);

        String imageUrl_normal = "photo_normal.jpg";
        String imageUrl_big = "photo_big.jpg";
        String imageUrl_400x400 = "photo_400x400.jpg";
        String resultImageUrl = "photo.jpg";
        // then
        assertThat(changeImageUrl.invoke(interactor, imageUrl_normal), equalTo(resultImageUrl));
        assertThat(changeImageUrl.invoke(interactor, imageUrl_big), equalTo(resultImageUrl));
        assertThat(changeImageUrl.invoke(interactor, imageUrl_400x400), equalTo(resultImageUrl));
    }

    @Test public void convertDate_test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        Method convertDate = SearchInteractor.class.getDeclaredMethod("convertDate", Date.class);
        convertDate.setAccessible(true);

        Date date = new Date();
        date.setTime(1494361405067L);
        String dateString = "Tue, 09 May, 11:23";
        // then
        assertTrue(convertDate.invoke(interactor, date).equals(dateString));
    }

    @Test public void getRawTweets_test() throws NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException {
        // given
        interactor = new SearchInteractor(mockedRepository);
        Method getRawTweets = SearchInteractor.class.getDeclaredMethod("getRawTweets", String.class);
        getRawTweets.setAccessible(true);
        // when
        Mockito.when(mockedRepository.getTweets(Mockito.anyString()))
                .thenReturn(Observable.empty());

        String REQUEST = "request";
        getRawTweets.invoke(interactor, REQUEST);
        // then
        Mockito.verify(mockedRepository).getTweets(REQUEST);
    }

    @Test public void convertTweets_EmptyTweet() {
        // given
        TestObserver<Tweet> testObserver = new TestObserver<>();
        Observable<SearchTweetModel> observableError = Observable.just(new SearchErrorTweetModel(null, 0));
        // when
        Observable<Tweet> tweetObservable = interactor.convertTweets(observableError);
        tweetObservable.subscribe(testObserver);
        // then
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        assertTrue(testObserver.values().get(0) instanceof EmptyTweet);
        assertThat(((EmptyTweet) (testObserver.values().get(0))).getCode(), equalTo(2));
    }

    @Test public void convertTweets() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        TestObserver<Tweet> testObserver = new TestObserver<>();
        // when
        Observable<Tweet> tweetObservable = interactor.convertTweets(observableTweetModel);
        tweetObservable.subscribe(testObserver);
        // then
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        assertTrue(testObserver.values().get(0) instanceof Tweet);
        assertThat(testObserver.values().get(0).getUserName(), equalTo(userName));
        assertThat(testObserver.values().get(0).getTweetText(), equalTo(tweetText));
        assertThat(testObserver.values().get(0).getUserImageUrl(), equalTo(usrImageUrl));
        assertThat(testObserver.values().get(0).getLikes(), equalTo(tweetLikes));
        assertThat(testObserver.values().get(0).getRetweets(), equalTo(tweetRetweets));
        assertThat(testObserver.values().get(0).getTweetDate(), equalTo(tweetDate));
    }

    @Test public void loadTweets_testEmptyResponse() throws IOException {
        // given
        interactor = new SearchInteractor(mockedRepository);
        // when
        Mockito.when(mockedRepository.getTweets(Mockito.anyString()))
                .thenReturn(Observable.empty());

        interactor.loadTweets("any Request")
                .subscribe(testObserver);
        // then
        testObserver.assertComplete();
        testObserver.assertValueCount(1);
        assertTrue(testObserver.values().get(0) instanceof EmptyTweet);
        assertThat(((EmptyTweet) (testObserver.values().get(0))).getCode(), equalTo(1)); // NO_TWEETS_CODE
    }
}