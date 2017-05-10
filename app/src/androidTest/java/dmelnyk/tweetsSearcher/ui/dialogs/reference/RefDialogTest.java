package dmelnyk.tweetsSearcher.ui.dialogs.reference;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import dmelnyk.tweetsSearcher.R;
import dmelnyk.tweetsSearcher.ui.search.SearchActivity;
import dmelnyk.tweetsSearcher.ui.web.WebViewActivity;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by dmitry on 05.05.17.
 */

@RunWith(AndroidJUnit4.class)
public class RefDialogTest {

    @Rule public IntentsTestRule<SearchActivity> rule =
            new IntentsTestRule<>(SearchActivity.class);

    List<String> refsList;
    String reference1 = "http://www.cyberforum.ru/android-dev/";
    String reference2 = "http://www.cyberforum.ru/";
    @Before public void setUp() {
        refsList = new ArrayList<>();
        refsList.add(reference1);
        refsList.add(reference2);
    }

    @Test public void testDialogShow() throws InterruptedException {
        // given
        RefDialog refDialog = RefDialog.getInstance(refsList);
        // when
        refDialog.show(rule.getActivity().getSupportFragmentManager(), "refDialog");
        // then
        Espresso.onView(withText(R.string.RefDialog_title))
                .check(matches(isDisplayed()));

        Espresso.onView(withText(reference1))
                .check(matches(isDisplayed()));

        Espresso.onView(withText(reference2))
                        .check(matches(isDisplayed()));
    }

    @Test public void testRunInTheWeb_clickOnFirstRef() throws InterruptedException {
        // given
        RefDialog refDialog = RefDialog.getInstance(refsList);
        // when
        refDialog.show(rule.getActivity().getSupportFragmentManager(), "refDialog");
        Espresso.onView(withText(reference1)).perform(click());
        // then
        intended(hasComponent(WebViewActivity.class.getName()));
        intended(hasExtras(hasEntry(equalTo(WebViewActivity.KEY_URL), equalTo(reference1))));
    }

    @Test public void testRunInTheWeb_clickOnSecondRef() {
        // given
        RefDialog refDialog = RefDialog.getInstance(refsList);
        // when
        refDialog.show(rule.getActivity().getSupportFragmentManager(), "refDialog");
        Espresso.onView(withText(reference2)).perform(click());
        // then
        intended(hasComponent(WebViewActivity.class.getName()));
        intended(hasExtras(hasEntry(equalTo(WebViewActivity.KEY_URL), equalTo(reference2))));
    }
}