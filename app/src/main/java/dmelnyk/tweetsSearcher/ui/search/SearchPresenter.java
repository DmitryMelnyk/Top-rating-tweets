package dmelnyk.tweetsSearcher.ui.search;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import dmelnyk.tweetsSearcher.business.ISearchInteractor;
import dmelnyk.tweetsSearcher.business.model.Tweet;
import dmelnyk.tweetsSearcher.ui.search.Contract.ISearchPresenter;
import dmelnyk.tweetsSearcher.utils.RxSchedulers;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by dmitry on 29.04.17.
 */
public class SearchPresenter implements ISearchPresenter {

    public final String TAG = this.getClass().getSimpleName();

    Contract.ISearchView view;

    private ISearchInteractor searchInteractor;
    private RxSchedulers rxSchedulers;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SearchPresenter(ISearchInteractor searchInteractor, RxSchedulers rxSchedulers) {
        this.searchInteractor = searchInteractor;
        this.rxSchedulers = rxSchedulers;
    }

    @Override
    public void bindView(Contract.ISearchView view) {
        this.view = view;
    }

    @Override
    public void unbindView() {
        compositeDisposable.clear();
        view = null;
    }

    // searching tweets in the net
    @Override
    public void loadTweets(Observable<CharSequence> observable) {

        // Observable for text changing with debounce 1500 millis.
        Observable<CharSequence> textChangeObservable = observable
                .debounce(1500, TimeUnit.MILLISECONDS)
                .filter(text -> text.length() > 2);

        Disposable disposable = textChangeObservable
                // display search progress
                .observeOn(rxSchedulers.getMainThreadScheduler())
                .doOnNext(ignore -> displaySearchProcessing())
                // searching tweets
                .observeOn(rxSchedulers.getIoSchedulers())
                .flatMap(request -> Observable.interval(300, TimeUnit.MILLISECONDS)
                        .zipWith(searchInteractor.loadTweets(request), (index, tweet) -> tweet))
                .observeOn(rxSchedulers.getMainThreadScheduler())
                // show tweets
                .subscribe(tweets -> displayTweets(tweets));

        compositeDisposable.add(disposable);
    }

    private void displaySearchProcessing() {
        view.initializeNonEmptyState();
        view.onHideKeyboard();
        view.onShowProgress();
        view.cleanRecycler();
    }

    private void displayTweets(Tweet tweets) {
        view.onHideProgress();
        view.onUpdateTweets(tweets);
    }

    // forwarding text from SearchEditTextField to SearchView
    @Override
    public void forwardInputData(Observable<CharSequence> observable) {

        compositeDisposable.add(observable
                .filter(text -> text.length() > 2)
                        .subscribe(
                        searchRequest -> {
                            Log.d(TAG, "searchRequest = " + searchRequest);
                            view.onChangeInputTextField(searchRequest);
                        })
        );
    }
}
