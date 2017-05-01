package dmelnyk.tweetsSearcher.data.network.models.response.search;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmitry on 30.04.17.
 */
public class SearchTweetModel {

    public SearchTweetModel(String textTweet, String dateCreation, int favoriteCount, int retweetCount) {
        this.dateCreation = dateCreation;
        this.likesCount = favoriteCount;
        this.retweetCount = retweetCount;
        this.textTweet = textTweet;
    }

    @SerializedName("favorite_count")
    private int likesCount;

    @SerializedName("retweet_count")
    private int retweetCount;

    @SerializedName("created_at")
    private String dateCreation;

    @SerializedName("text")
    private String textTweet;

    @SerializedName("user")
    private User user;

    public String getDateCreation() {
        return dateCreation;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public String getTextTweet() {
        return textTweet;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class User {

        public User(String userName, String userImageUrl) {
            this.userImageUrl = userImageUrl;
            this.userName = userName;
        }

        @SerializedName("name")
        private String userName;

        @SerializedName("profile_image_url")
        private String userImageUrl;


        public String getUserImageUrl() {
            return userImageUrl;
        }

        public String getUserName() {
            return userName;
        }

        @Override
        public String toString() {
            return "User{" +
                    "userName='" + userName + '\'' +
                    ", userImageUrl='" + userImageUrl + '\'' +
                    '}';
        }
    }
}
