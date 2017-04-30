package dmelnyk.tweetsSearcher.ui.search;

import io.reactivex.Observable;

/**
 * Created by dmitry on 29.04.17.
 */

public class Contract {

    public interface ISearchView {
        void onAnimateSearchView();

        void onShopProgress();
        void onHideProgress();

        void onShowErrorToast(String message);
    }

    public interface ISearchPresenter {
        void bindView(ISearchView view);
        void unbindView();

        void loadTweets(Observable<CharSequence> observable);
    }
}
