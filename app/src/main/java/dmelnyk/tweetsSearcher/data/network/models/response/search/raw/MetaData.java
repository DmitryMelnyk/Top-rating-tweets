package dmelnyk.tweetsSearcher.data.network.models.response.search.raw;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmitry on 03.05.17.
 */
public class MetaData {

    @SerializedName("next_results")
    private String nextResults;

    @Override
    public String toString() {
        return "MetaData{" +
                "next results='" + nextResults + '\'' +
                '}';
    }
}
