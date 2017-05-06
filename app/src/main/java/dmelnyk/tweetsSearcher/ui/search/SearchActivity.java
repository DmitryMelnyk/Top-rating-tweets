package dmelnyk.tweetsSearcher.ui.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmelnyk.tweetsSearcher.R;
import dmelnyk.tweetsSearcher.application.MyApp;
import dmelnyk.tweetsSearcher.business.model.Tweet;
import dmelnyk.tweetsSearcher.ui.search.di.SearchModule;
import dmelnyk.tweetsSearcher.ui.search.utils.RetainFragment;
import dmelnyk.tweetsSearcher.ui.search.utils.TweetAdapter;
import io.reactivex.Observable;

public class SearchActivity extends AppCompatActivity implements Contract.ISearchView {

    private final String TAG = this.getClass().getSimpleName();
    private final String FRAGMENT_TAG = TAG + "retain fragment";
    private final String STATE_EMPTY = TAG + "empty";
    private final String STATE_NON_EMPTY = TAG + "not empty";

    @Inject Contract.ISearchPresenter presenter;

    @BindView(R.id.searchField) EditText searchField;
    @BindView(R.id.twitterRecycler) RecyclerView twitterRecycler;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    private EditText searchText;
    private SearchView searchView;
    private MenuItem searchItem;
    private RetainFragment retainFragment;
    private TweetAdapter adapter;

    // initial state
    private String state = STATE_EMPTY;
    private String searchRequest = "";
    private ArrayList<Tweet> tweets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // for injecting dependency
        MyApp.get(this).getAppComponent().add(new SearchModule()).inject(this);

        // instantiate 'retainFragment' for saving/restoring data
        instantiateRetainFragment();

        // restoring state, tweets
        restoreDataFromFragment();
        // create configuration: EMPTY-screen or NONEMPTY recyclerView with data
        restoreState(state);

        presenter.bindView(this);
    }

    // create fragment to save data
    private void instantiateRetainFragment() {
        retainFragment = (RetainFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (retainFragment == null) {
            retainFragment = new RetainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(retainFragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    // restoring data
    private void restoreDataFromFragment() {
        if (retainFragment.getSavedState() != null) {
            state = retainFragment.getSavedState();
            tweets = retainFragment.getSavedTweets();
            searchRequest = retainFragment.getSavedSearchRequest();
            Log.d(TAG, "state = " + state);
        }
    }

    public void restoreState(String state) {
        if (state.equals(STATE_EMPTY)) {
            initializeEmptyState();
        } else {
            initializeNonEmptyState();
        }
    }

    private void initializeEmptyState() {
        // show SearchEditText()
        initializeSearchEditText();
    }

    public void initializeNonEmptyState() {
        // hide SearchEditText()
        hideSearchEditText();
        initializeRecyclerView();
        initializeSwipeRefreshLayout();
        // show data in RecyclerView
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        saveDataInFragment();
        presenter.unbindView();
        super.onPause();
    }

    private void saveDataInFragment() {
        retainFragment.saveState(state);
        retainFragment.saveTweets(tweets);
        retainFragment.saveRequest(searchRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.menu, menu);

        searchItem = menu.findItem(R.id.actionSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchText = (EditText) searchView.findViewById(
                android.support.v7.appcompat.R.id.search_src_text);

        searchText.setGravity(View.TEXT_ALIGNMENT_CENTER);

        if (state.equals(STATE_EMPTY)) {
            searchItem.setVisible(false);
        }

        // observable can be created after initializing 'searchText'
        createObservable();
        return super.onCreateOptionsMenu(menu);
    }

    private void createObservable() {
        // creating Observable from SearchView's text, refresh-button and swipeRefreshLayout
        Observable<CharSequence> observableTextView = RxTextView.textChanges(searchText)
                .doOnNext(request -> {
                            saveRequest(request);
                            Log.d(TAG, "request = " + request);
                });
        Observable<CharSequence> observableSwipeRefresh = Observable.create(
                emitter ->
                        swipeRefreshLayout.setOnRefreshListener(
                                () -> {
                                    // load swipe animation only if requestField isn't empty
                                    if (!searchRequest.isEmpty()) {
                                        searchText.setText(searchRequest);
                                        emitter.onNext(searchRequest);
                                    } else {
                                        // stop refresh and show message
                                        swipeRefreshLayout.setRefreshing(false);
                                        onShowErrorToast("Search request is empty. Please, enter some tag to search");
                                    }
                                }
                        ));

        Observable<CharSequence> compositeObservable = Observable
                .merge(observableTextView, observableSwipeRefresh);
        presenter.loadTweets(compositeObservable);
    }

    @NonNull
    private void saveRequest(CharSequence request) {
        if (request.length() > 0) {
            searchRequest = request.toString();
        }
    }

    private void initializeSearchEditText() {
        Observable<CharSequence> textChanged = RxTextView.textChanges(searchField);
        presenter.forwardInputData(textChanged);
    }

    private void initializeSwipeRefreshLayout() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    private void initializeRecyclerView() {
        adapter = new TweetAdapter(tweets, this);
        twitterRecycler.setLayoutManager(new LinearLayoutManager(this));
        twitterRecycler.setAdapter(adapter);
    }

    @Override
    public void onUpdateTweets(Tweet tweet) {
        tweets.add(tweet);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onHideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)
                this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @Override
    public void cleanRecycler() {
        tweets.clear();
    }

    @Override
    public void onShowProgress() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onHideProgress() {
        // cancelling swipe-animation
        swipeRefreshLayout.setRefreshing(false);
        searchView.onActionViewCollapsed();
    }

    @Override
    public void onShowErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShowErrorToast(int message) {
        switch (message) {
            case -1: // bad searchRequest
                Toast.makeText(this, getString(R.string.bad_request), Toast.LENGTH_LONG).show();
                break;
            case -2: // no internet Connection;
                Toast.makeText(this, "no internet connection", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onChangeInputTextField(CharSequence request) {
        // display and forward data to searchView
        state = STATE_NON_EMPTY;
        searchItem.setVisible(true);
        searchText.setText(request, TextView.BufferType.EDITABLE);
        searchField.requestFocus();
    }

    private void hideSearchEditText() {
        searchField.setVisibility(View.GONE);
    }
}
