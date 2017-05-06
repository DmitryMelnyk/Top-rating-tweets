package dmelnyk.tweetsSearcher.ui.search;

import android.content.Context;
import android.support.test.espresso.Espresso;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.inputmethod.InputMethodManager;

import junit.framework.Assert;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import dmelnyk.tweetsSearcher.R;
import dmelnyk.tweetsSearcher.business.model.Tweet;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by dmitry on 02.05.17.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SearchActivityTest {

    @Rule
    public ActivityTestRule<SearchActivity> rule = new ActivityTestRule<SearchActivity>(SearchActivity.class);

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

    @Test
    public void initializeSearchEditText() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        Method initializeSearchEditText = SearchActivity.class.getDeclaredMethod("initializeSearchEditText");
        initializeSearchEditText.setAccessible(true);
        // when
        initializeSearchEditText.invoke(searchActivity);
        // then
        Espresso.onView(withId(R.id.searchField))
                .check(matches(isDisplayed()));
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

    @Test public void onHideKeyboard() {
        // given
        InputMethodManager imm = (InputMethodManager) searchActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // when
        Espresso.onView(withId(R.id.searchField))
                .perform(click());
        // then
        Assert.assertTrue(imm.isAcceptingText());

        // when
//        searchActivity.runOnUiThread(() -> onHideKeyboard());
        // TODO: test is hanging
        // then
//        Assert.assertFalse(imm.isAcceptingText());
    }

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

    @Test
    public void onShowProgress() {
        // before
        Espresso.onView(withId(R.id.searchField))
                .perform(typeText("text"));

        Espresso.onView(withId(R.id.searchField))
                .check(matches(isDisplayed()));
        // when
        searchActivity.runOnUiThread(
                () -> searchActivity.onShowProgress());
        // TODO: how te check icon changing
    }

    @Test
    public void onShowErrorToast() {
        // given
        String message = "is toast";
        // when
        searchActivity.runOnUiThread(() -> searchActivity.onShowErrorToast(message));
        // then
        Espresso.onView(withText(message))
                .inRoot(withDecorView(not(is(searchActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void onChangeInputTextField() throws NoSuchFieldException, InterruptedException {
        // given
        CharSequence searchRequest = "#android";
        // when
        searchActivity.runOnUiThread(() -> searchActivity
                .onChangeInputTextField(searchRequest));
        // then
        Espresso.onView(withText(searchRequest.toString()))
                .check(matches(isDisplayed()));
//        Thread.sleep(2000);
    }

    @Test
    public void hideSearchEditText() throws NoSuchMethodException {
        // before
        Espresso.onView(withId(R.id.searchField))
                .check(matches(isDisplayed()));
        // given
        Method hideSearchEditText = SearchActivity.class.getDeclaredMethod("hideSearchEditText");
        hideSearchEditText.setAccessible(true);
        // when
        searchActivity.runOnUiThread(
                () -> {
                    try {
                        hideSearchEditText.invoke(searchActivity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        // then
        Espresso.onView(withId(R.id.searchField))
                .check(matches(CoreMatchers.not(isDisplayed())));
    }
}