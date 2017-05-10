package dmelnyk.tweetsSearcher.business.model;

/**
 * Created by dmitry on 04.05.17.
 */

// Object indicates that server didn't find any tweets
public class EmptyTweet extends Tweet {

    public static final int NO_TWEETS_CODE = 1;
    public static final int NO_INTERNET_CONNECTION_CODE = 2;

    private int code;

    public EmptyTweet(int code) {
        super();
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
