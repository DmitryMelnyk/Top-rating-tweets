package dmelnyk.tweetsSearcher.ui.search.dagger;

import dagger.Module;
import dagger.Provides;
import dmelnyk.tweetsSearcher.ui.search.Contract;
import dmelnyk.tweetsSearcher.ui.search.SearchPresenter;

/**
 * Created by dmitry on 29.04.17.
 */


@Module
public class SearchModule {

    // provide-methods for getting instance of Repository, SearchInteractor, SearchPresenterCache etc.

    @Provides
    Contract.ISearchPresenter provideISearchPresenter() {
        return new SearchPresenter();
    }
}
