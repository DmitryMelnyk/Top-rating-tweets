package dmelnyk.tweetsSearcher.ui.search;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import junit.framework.Assert;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import dmelnyk.tweetsSearcher.R;
import dmelnyk.tweetsSearcher.business.model.Tweet;
import dmelnyk.tweetsSearcher.ui.web.WebViewActivity;

import static android.R.id.message;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by dmitry on 02.05.17.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SearchActivityTest {

    @Rule
    public IntentsTestRule<SearchActivity> rule = new IntentsTestRule<SearchActivity>(SearchActivity.class);

    private Tweet tweet1;
    private Tweet tweet2;
    private SearchActivity searchActivity;

    @Before
    public void setTweets() {
        tweet1 = Tweet.Builder()
                .withText("Softwareentwickler Schwerpunkt Android in Hamburg, #Softwareentwicklung #IBM #Android #RxJava")
                .withName("John Doe")
                .withImageUrl("url")
                .withDate("Fri Apr 28 00:16:41 +0000 2017")
                .withLikes(12)
                .withRetweets(3)
                .build();

        tweet2 = Tweet.Builder()
                .withText("tweet2")
                .withName("Jane Doe")
                .withImageUrl("url")
                .withDate("Sun Apr 28 00:16:41 +0000 2017")
                .withLikes(12)
                .withRetweets(3)
                .build();

        searchActivity = rule.getActivity();
    }

    @Test public void instantiateRetainFragment() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        // given:
        Method instantiateRetainFragment = SearchActivity.class.getDeclaredMethod("instantiateRetainFragment");
        instantiateRetainFragment.setAccessible(true);

        Field TAG = SearchActivity.class.getDeclaredField("FRAGMENT_TAG");
        TAG.setAccessible(true);
        // when:
        instantiateRetainFragment.invoke(searchActivity);
        // then:
        Fragment savedFragment = searchActivity.getSupportFragmentManager().findFragmentByTag(
                (String) TAG.get(searchActivity));
        Assert.assertTrue(savedFragment != null);
    }

    @Test public void onSaveDataInFragment_onRestoreDataFromFragment() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        // given
        Method saveDataInFragment = SearchActivity.class.getDeclaredMethod("saveDataInFragment");
        saveDataInFragment.setAccessible(true);

        Method restoreDataFromFragment = SearchActivity.class.getDeclaredMethod("restoreDataFromFragment");
        restoreDataFromFragment.setAccessible(true);

        Field tweets = SearchActivity.class.getDeclaredField("tweets");
        tweets.setAccessible(true);

        Field STATE_NON_EMPTY = SearchActivity.class.getDeclaredField("STATE_NON_EMPTY");
        STATE_NON_EMPTY.setAccessible(true);

        // initialize NonEmptyState for retrieving data from fragment
        Field state = SearchActivity.class.getDeclaredField("state");
        state.setAccessible(true);
        state.set(searchActivity, STATE_NON_EMPTY.get(searchActivity));

        ArrayList<Tweet> tweetList = new ArrayList<>();
        // before
        Assert.assertTrue(((ArrayList<Tweet>) tweets.get(searchActivity)).isEmpty());

        // when added 1 tweet
        tweetList.add(tweet1);
        tweets.set(searchActivity, tweetList);
        Assert.assertTrue(((ArrayList<Tweet>) tweets.get(searchActivity)).size() == 1);
            // saving data to fragment
        saveDataInFragment.invoke(searchActivity);
            // clearing tweets
        tweetList.clear();
        tweets.set(searchActivity, tweetList);
        Assert.assertTrue(((ArrayList<Tweet>) tweets.get(searchActivity)).isEmpty());

        // then after restoring data
        restoreDataFromFragment.invoke(searchActivity);
        Assert.assertTrue(((ArrayList<Tweet>) tweets.get(searchActivity)).size() == 1);

        // when added two tweets
        tweetList.add(tweet1);
        tweetList.add(tweet2);
        tweets.set(searchActivity, tweetList);
            // saving 2 tweets
        saveDataInFragment.invoke(searchActivity);
            // clearing tweets
        tweetList.clear();
        tweets.set(searchActivity, tweetList);
        restoreDataFromFragment.invoke(searchActivity);

        // then
        Assert.assertTrue(((ArrayList<Tweet>) tweets.get(searchActivity)).size() == 2);
    }

    @Test public void onUpdateTweets() throws NoSuchMethodException, InterruptedException {
        // given
        searchActivity.runOnUiThread(() -> {
            searchActivity.initializeNonEmptyState();
        });
        // when
        searchActivity.runOnUiThread(() -> {
            searchActivity.onUpdateTweets(tweet1);
            searchActivity.onUpdateTweets(tweet2);
        });
        // then
        Espresso.onView(withText(tweet1.getTweetText()))
                .check(matches(isDisplayed()));

        Espresso.onView(withText(tweet2.getTweetText()))
                .check(matches(isDisplayed()));
    }

//    @Test public void onHideKeyboard() throws InterruptedException {
//        // given
//        InputMethodManager imm = (InputMethodManager) searchActivity
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        // when
//        Espresso.onView(ViewMatchers.withId(R.id.actionSearch))
//                .perform(ViewActions.click());
//        Espresso.onView(ViewMatchers.withId(android.support.v7.appcompat.R.id.search_src_text))
//                .perform(ViewActions.typeText("some text"));
//        // then
//        Assert.assertTrue(imm.isAcceptingText());
//        // when
//        searchActivity.runOnUiThread(() -> searchActivity.onHideProgress());
//        Assert.assertTrue(imm.isAcceptingText());
//        searchActivity.runOnUiThread(() -> searchActivity.onHideKeyboard());
//        Assert.assertFalse(imm.isAcceptingText());
//    }

    @Test
    public void cleanRecycler() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // given
        ArrayList<Tweet> tweetList = new ArrayList<>();
        tweetList.add(tweet1);
        tweetList.add(tweet1);
        tweetList.add(tweet1);

        Method cleanRecycler = SearchActivity.class.getDeclaredMethod("cleanRecycler");
        cleanRecycler.setAccessible(true);

        Field tweets = SearchActivity.class.getDeclaredField("tweets");
        tweets.setAccessible(true);
        // when added 3 tweets to 'tweets':
        tweets.set(rule.getActivity(), tweetList);
        // then:
        Assert.assertTrue(((ArrayList<Tweet>) tweets.get(searchActivity)).size() == 3);

        // when:
        cleanRecycler.invoke(rule.getActivity());
        // then:
        Assert.assertTrue(((ArrayList<Tweet>) tweets.get(searchActivity)).isEmpty());
    }

    @Test public void onShowProgress_onHideProgress() throws NoSuchFieldException, IllegalAccessException {
        // given
        Field searchRequest = SearchActivity.class.getDeclaredField("searchRequest");
        searchRequest.setAccessible(true);
        searchRequest.set(searchActivity, "request");
        // when
        searchActivity.runOnUiThread(
                () -> searchActivity.onShowProgress());
        // then
        Awaitility.await().atLeast(300, TimeUnit.MILLISECONDS);
        Assert.assertTrue(searchActivity.swipeRefreshLayout.isRefreshing());
        // when
        searchActivity.runOnUiThread(
                () -> searchActivity.onHideProgress());
        // then
        Awaitility.await().atLeast(300, TimeUnit.MILLISECONDS);
        Assert.assertFalse(searchActivity.swipeRefreshLayout.isActivated());
    }

    @Test public void onShowErrorToast() {
        // given
        String message1 = searchActivity.getString(R.string.toast_empty);
        String message2 = searchActivity.getString(R.string.toast_empty);
        String message3 = searchActivity.getString(R.string.toast_empty);
        // when
        searchActivity.runOnUiThread(() -> searchActivity.onShowErrorToast(1));
        // then
        Espresso.onView(withText(message))
                .inRoot(withDecorView(not(is(searchActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test public void setReference() {
        // given
        String url = "https://google.com.ua";
        // when
        searchActivity.setReference(url);
        // then
        Intents.intended(IntentMatchers.hasComponent(WebViewActivity.class.getName()));
        Intents.intended(IntentMatchers.hasExtras(hasEntry(
                equalTo(WebViewActivity.KEY_URL), equalTo(url))
        ));
    }
}