package dmelnyk.tweetsSearcher.ui.search.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import dmelnyk.tweetsSearcher.business.model.Tweet;

/**
 * Created by dmitry on 29.04.17.
 */

public class RetainFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private String state;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Nullable
    public ArrayList<Tweet> getSavedTweets() {
        return tweets;
    }

    public String getSavedState() {
        return state;
    }

    public void saveTweets(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }

    public void saveState(String state) {
        this.state = state;
    }
}
