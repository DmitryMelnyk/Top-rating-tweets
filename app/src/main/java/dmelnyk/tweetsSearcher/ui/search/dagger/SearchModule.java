package dmelnyk.tweetsSearcher.ui.search.dagger;

import dagger.Module;
import dagger.Provides;
import dmelnyk.tweetsSearcher.business.FakeSearchInteractor;
import dmelnyk.tweetsSearcher.business.ISearchInteractor;
import dmelnyk.tweetsSearcher.ui.search.Contract;
import dmelnyk.tweetsSearcher.ui.search.SearchPresenter;
import dmelnyk.tweetsSearcher.utils.RxSchedulers;

/**
 * Created by dmitry on 29.04.17.
 */


@Module
public class SearchModule {

    // provide-methods for getting instance of Repository, SearchInteractor, SearchPresenterCache etc.

    @Provides
    @SearchScope
    ISearchInteractor providesISearchInteractor() {
        return new FakeSearchInteractor();
    }

    @Provides
    @SearchScope
    Contract.ISearchPresenter provideISearchPresenter(ISearchInteractor iSearchInteractor,
                                                      RxSchedulers rxSchedulers) {
        return new SearchPresenter(iSearchInteractor, rxSchedulers);
    }
}
