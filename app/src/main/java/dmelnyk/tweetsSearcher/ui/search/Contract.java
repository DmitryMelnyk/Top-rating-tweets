package dmelnyk.tweetsSearcher.ui.search;

import io.reactivex.Observable;

/**
 * Created by dmitry on 29.04.17.
 */

public class Contract {

    public interface ISearchView {
        void onAnimateSearchView();

        void onShowProgress();
        void onHideProgress();

        void onShowErrorToast(String message);

        void onChangeInputTextField(CharSequence request);
    }

    public interface ISearchPresenter {
        void bindView(ISearchView view);
        void unbindView();

        void loadTweets(Observable<CharSequence> observable);
        void forwardInputData(Observable<CharSequence> observable);
    }
}
