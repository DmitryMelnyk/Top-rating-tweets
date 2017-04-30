package dmelnyk.tweetsSearcher.ui.search;

import java.util.concurrent.TimeUnit;

import dmelnyk.tweetsSearcher.ui.search.Contract.ISearchPresenter;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by dmitry on 29.04.17.
 */
public class SearchPresenter implements ISearchPresenter {

    Contract.ISearchView view;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean animationStarted;

    @Override
    public void bindView(Contract.ISearchView view) {
        this.view = view;
    }

    @Override
    public void unbindView() {
        compositeDisposable.clear();
        view = null;
    }

    @Override
    public void loadTweets(Observable<CharSequence> observable) {
        compositeDisposable.add(
                observable
                        .filter(text -> text.length() != 0)
                        .debounce(500, TimeUnit.MILLISECONDS)
                        // It's mock. TODO: replace onShowProgress with Interceptor -> searchTweets
                        .subscribe(value -> view.onShowProgress()));
    }

    @Override
    public void forwardInputData(Observable<CharSequence> observable) {
        compositeDisposable.add(
                observable
                        .filter(text -> text.length() != 0)
                        // run animation only once
                        .doOnNext(ignore -> {
                            if (!animationStarted) {
                                animationStarted = true;
                                view.onAnimateSearchView();
                            }
                        })
                        .subscribe(
                        searchRequest -> {
                            view.onChangeInputTextField(searchRequest);
//                            view.onAnimateSearchView();
                        })
        );
    }
}
