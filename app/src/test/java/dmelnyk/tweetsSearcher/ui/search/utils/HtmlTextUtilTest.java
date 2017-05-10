package dmelnyk.tweetsSearcher.ui.search.utils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by dmitry on 06.05.17.
 */
public class HtmlTextUtilTest {

    private String textWithRefs;
    private String textWithoutRefs;
    private String textWithTwoReference;

    @Before public void setUp() {
        textWithoutRefs = "Just text http//error.reference.com";
        textWithRefs = "Text, some text with ref: https://www.w3schools.com/tags/tag_u.asp and ok";
        textWithTwoReference = "some text plus ref: http://www.cyberforum.ru/android-dev/ https://www.youtube.com/watch?v=qJRqdEcoq7A";
    }

    @Test public void findReferences() {
        // when
        ArrayList<String> refs = HtmlTextUtil.findReferences(textWithoutRefs);
        // then
        Assert.assertEquals(refs.isEmpty(), true);

        // when
        refs = HtmlTextUtil.findReferences(textWithRefs);
        // then
        Assert.assertFalse(refs.isEmpty());
        Assert.assertEquals(refs.get(0), "https://www.w3schools.com/tags/tag_u.asp");
        // when
        refs = HtmlTextUtil.findReferences(textWithTwoReference);
        // then
        Assert.assertEquals(2, refs.size());
    }

}