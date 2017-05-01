package dmelnyk.tweetsSearcher.business.model;

/**
 * Created by dmitry on 01.05.17.
 */
public class Tweet {
    private String userName;
    private String userImageUrl;
    private String tweetDate;
    private String tweetText;
    private int likes;
    private int retweets;

    private Tweet() {
    }

    public int getLikes() {
        return likes;
    }

    public int getRetweets() {
        return retweets;
    }

    public String getTweetDate() {
        return tweetDate;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getTweetText() {
        return tweetText;
    }

    public static newBuilder Builder() {
        return new Tweet().new newBuilder();
    }

    public class newBuilder {

        private newBuilder() {}

        public newBuilder withText(String text) {
            tweetText = text;
            return this;
        }

        public newBuilder withName(String name) {
            userName = name;
            return this;
        }

        public newBuilder withImageUrl(String url) {
            userImageUrl = url;
            return this;
        }

        public newBuilder withDate(String date) {
            tweetDate = date;
            return this;
        }

        public newBuilder withLikes(int likesCount) {
            likes = likesCount;
            return this;
        }

        public newBuilder withRetweets(int retweetsCount) {
            retweets = retweetsCount;
            return this;
        }

        public Tweet build() {
            return Tweet.this;
        }
    }
}
