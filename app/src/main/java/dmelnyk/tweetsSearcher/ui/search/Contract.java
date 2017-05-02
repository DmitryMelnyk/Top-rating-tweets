package dmelnyk.tweetsSearcher.ui.search;

import dmelnyk.tweetsSearcher.business.model.Tweet;
import io.reactivex.Observable;

/**
 * Created by dmitry on 29.04.17.
 */

public class Contract {

    public interface ISearchView {
        void onShowProgress();
        void onHideProgress();

        void onShowErrorToast(String message);

        void onChangeInputTextField(CharSequence request);

        void onUpdateTweets(Tweet tweets);

        void cleanRecycler();

        void initializeNonEmptyState();

        void onHideKeyboard();
    }

    public interface ISearchPresenter {
        void bindView(ISearchView view);
        void unbindView();

        void loadTweets(Observable<CharSequence> observable);
        void forwardInputData(Observable<CharSequence> observable);
    }
}
