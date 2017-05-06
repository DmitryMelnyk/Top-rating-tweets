package dmelnyk.tweetsSearcher.ui.search.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dmelnyk.tweetsSearcher.business.ISearchInteractor;
import dmelnyk.tweetsSearcher.business.SearchInteractor;
import dmelnyk.tweetsSearcher.data.repositories.search.ISearchRepository;
import dmelnyk.tweetsSearcher.data.repositories.search.SearchRepository;
import dmelnyk.tweetsSearcher.data.repositories.search.core.NetworkUtil;
import dmelnyk.tweetsSearcher.ui.search.Contract;
import dmelnyk.tweetsSearcher.ui.search.SearchPresenter;
import dmelnyk.tweetsSearcher.utils.RxSchedulers;

/**
 * Created by dmitry on 29.04.17.
 */


@Module
public class SearchModule {

    @Provides
    @SearchScope
    NetworkUtil providesNetworkUtil(Context context) {
        return new NetworkUtil(context);
    }

    @Provides
    @SearchScope
    ISearchRepository providesISearchRepository(NetworkUtil networkUtil) {
        return new SearchRepository(networkUtil);
    }

    @Provides
    @SearchScope
    ISearchInteractor providesISearchInteractor(ISearchRepository iSearchRepository) {
        return new SearchInteractor(iSearchRepository);
    }

    @Provides
    @SearchScope
    Contract.ISearchPresenter provideISearchPresenter(ISearchInteractor iSearchInteractor,
                                                      RxSchedulers rxSchedulers) {
        return new SearchPresenter(iSearchInteractor, rxSchedulers);
    }
}
