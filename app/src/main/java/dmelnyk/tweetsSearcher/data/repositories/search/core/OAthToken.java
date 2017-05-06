package dmelnyk.tweetsSearcher.data.repositories.search.core;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmitry on 03.05.17.
 */

public class OAthToken {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAuthorization() {
        return getTokenType() + " " + getAccessToken();
    }
}
