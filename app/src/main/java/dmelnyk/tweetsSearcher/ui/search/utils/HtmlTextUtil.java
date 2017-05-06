package dmelnyk.tweetsSearcher.ui.search.utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by dmitry on 06.05.17.
 */

public class HtmlTextUtil {

    public static ArrayList<String> findReferences(String text) {
        ArrayList<String> refList = new ArrayList<>();
        // splitting words ro check if them contain ref
        String[] words = text.split(" ");

        String refMatcherHttp = "http://";
        String refMatcherHttps = "https://";

        for (int i = 0; i < words.length; i++) {
            // not full reference
            if (!words[i].endsWith("…") &&
                    (words[i].startsWith(refMatcherHttp) ||
                    words[i].startsWith(refMatcherHttps))) {
                refList.add(words[i]);
                Log.d("228", "" + words[i] + " = " + words[i].endsWith("..."));
            }
        }
        return refList;
    }

    public static String convertToHtmlLikeText(String text) {
        ArrayList<String> references = findReferences(text);

        // if text contains references
        if (!references.isEmpty()) {
            String formattedText = "";

            for (String ref : references) {
                // add underline and blue color
                String formattedRef = "<font color=#2bb1ff><u>" + ref + "</u></font>";
                formattedText = text.replace(ref, formattedRef);
            }

            return formattedText;
        }

        return text;
    }

    public static Spanned getHtmlFormattedText(String text) {
        String htmlLikeText = convertToHtmlLikeText(text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(htmlLikeText, Html.FROM_HTML_MODE_LEGACY);
        }
        return Html.fromHtml(htmlLikeText);
    }
}