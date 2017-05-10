package dmelnyk.tweetsSearcher.ui.search;

import dmelnyk.tweetsSearcher.business.model.Tweet;
import io.reactivex.Observable;

/**
 * Created by dmitry on 29.04.17.
 */

public class Contract {

    public interface ISearchView {
        void onHideProgress();

        void onShowErrorToast(int message);

        void onUpdateTweets(Tweet tweets);

        void cleanRecycler();

        void initializeNonEmptyState();

        void onHideKeyboard();

        void onShowProgress();
    }

    public interface ISearchPresenter {
        void bindView(ISearchView view);
        void unbindView();

        void loadTweets(Observable<CharSequence> observable);
    }
}
