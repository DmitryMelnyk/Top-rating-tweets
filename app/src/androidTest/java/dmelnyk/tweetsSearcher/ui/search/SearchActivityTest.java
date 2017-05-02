package dmelnyk.tweetsSearcher.ui.search;

import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.inject.Inject;

import dmelnyk.tweetsSearcher.business.model.Tweet;

/**
 * Created by dmitry on 02.05.17.
 */

@RunWith(JUnit4.class)
public class SearchActivityTest {

    @Inject ActivityTestRule rule;

    Tweet tweet;

    @Before public void setTweets() {
        tweet = Tweet.Builder()
                .withText("Softwareentwickler Schwerpunkt Android in Hamburg, #Softwareentwicklung #IBM #Android #RxJava")
                .withName("John Doe")
                .withImageUrl("url")
                .withDate("Fri Apr 28 00:16:41 +0000 2017")
                .withLikes(12)
                .withRetweets(3)
                .build();
    }


}