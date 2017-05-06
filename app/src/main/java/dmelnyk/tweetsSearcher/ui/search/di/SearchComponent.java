package dmelnyk.tweetsSearcher.ui.search.di;

import dagger.Subcomponent;
import dmelnyk.tweetsSearcher.ui.search.SearchActivity;

/**
 * Created by dmitry on 29.04.17.
 */

@Subcomponent(modules = SearchModule.class)
@SearchScope
public interface SearchComponent {

    void inject(SearchActivity searchActivity);
}
