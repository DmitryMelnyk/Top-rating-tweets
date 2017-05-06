package dmelnyk.tweetsSearcher.data.network.models.response.search;

/**
 * Created by dmitry on 03.05.17.
 */

public class SearchErrorTweetModel extends RuntimeException {

    private final String description;
    private final int code;

    public SearchErrorTweetModel(String description, int code) {
        this.description = description;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
