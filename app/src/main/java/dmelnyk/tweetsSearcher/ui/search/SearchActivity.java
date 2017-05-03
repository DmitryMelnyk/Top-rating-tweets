package dmelnyk.tweetsSearcher.ui.search;

import android.content.Context;
import android.os.Bundle;
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

import com.jakewharton.rxbinding2.view.RxMenuItem;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmelnyk.tweetsSearcher.R;
import dmelnyk.tweetsSearcher.application.MyApp;
import dmelnyk.tweetsSearcher.business.model.Tweet;
import dmelnyk.tweetsSearcher.ui.search.dagger.SearchModule;
import dmelnyk.tweetsSearcher.ui.search.utils.RetainFragment;
import dmelnyk.tweetsSearcher.ui.search.utils.TweetAdapter;
import io.reactivex.Observable;

import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;

public class SearchActivity extends AppCompatActivity implements Contract.ISearchView {

    private final String TAG = this.getClass().getSimpleName();
    private final String FRAGMENT_TAG = TAG + "retain fragment";
    private final String STATE_EMPTY = TAG + "empty";
    private final String STATE_NON_EMPTY = TAG + "not empty";

    @Inject Contract.ISearchPresenter presenter;

    @BindView(R.id.searchField) EditText searchField;
    @BindView(R.id.twitterRecycler) RecyclerView twitterRecycler;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    private MenuItem refreshItem;
    private EditText searchText;
    private SearchView searchView;
    private MenuItem searchItem;
    private RetainFragment retainFragment;
    private TweetAdapter adapter;

    // initial state
    private String state = STATE_EMPTY;
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
            Log.d(TAG, "state = " + state);
            Log.d(TAG, "tweets = " + tweets.get(0).getUserName());
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.menu, menu);

        // initialize refreshItem item for replace him by 'R.layout.action_view_progress'
        refreshItem = menu.findItem(R.id.refreshButton);
        searchItem = menu.findItem(R.id.actionSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchText = (EditText) searchView.findViewById(
                android.support.v7.appcompat.R.id.search_src_text);

        if (state.equals(STATE_EMPTY)) {
            searchItem.setVisible(false);
        }

        // hide 'X'-button in SearchView
        View searchClose = searchView.findViewById(
                android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setEnabled(false);
        searchClose.setAlpha(0f);

        // observable can be created after initializing 'searchText'
        createObservable();
        return super.onCreateOptionsMenu(menu);
    }

    private void createObservable() {
        // creating Observable from SearchView's text, refresh-button and swipeRefreshLayout
        Observable<CharSequence> observableTextView = RxTextView.textChanges(searchText);
        Observable<CharSequence> observableRefreshClick = RxMenuItem.clicks(refreshItem)
                .flatMap(ignore -> Observable.just(searchText.getText().toString()));
        Observable<CharSequence> observableSwipeRefresh = Observable.create(
                emitter ->
                        swipeRefreshLayout.setOnRefreshListener(
                                () -> {
                                    String request = searchText.getText().toString();
                                    // load swipe animation only if requestField isn't empty
                                    if (!request.isEmpty()) {
                                        emitter.onNext(request);
                                    } else {
                                        // stop refresh and show message
                                        swipeRefreshLayout.setRefreshing(false);
                                        onShowErrorToast("Search request is empty. Please, enter some tag to search");
                                    }
                                }
                        ));

        Observable<CharSequence> compositeObservable = Observable
                .merge(observableTextView, observableRefreshClick, observableSwipeRefresh);
        presenter.loadTweets(compositeObservable);
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
        adapter = new TweetAdapter(tweets);
        twitterRecycler.setLayoutManager(new LinearLayoutManager(this));
        twitterRecycler.setAdapter(adapter);
    }

    @Override
    public void onUpdateTweets(Tweet tweet) {
        state = STATE_NON_EMPTY;

        tweets.add(tweet);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onHideKeyboard() {
        View viewWithFicus = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewWithFicus.getWindowToken(),  HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void cleanRecycler() {
        tweets.clear();
    }

    @Override
    public void onShowProgress() {
        // do not show refresh-icon animation if user swiped to refresh.
        if (!swipeRefreshLayout.isRefreshing()) {
            MenuItemCompat.setActionView(
                    refreshItem, R.layout.action_view_progress);
        }
    }

    @Override
    public void onHideProgress() {
        MenuItemCompat.setActionView(
                refreshItem, null);
        // cancelling swipe-animation
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onShowErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onChangeInputTextField(CharSequence request) {
        // display and forward data to searchView
        searchItem.setVisible(true);
        searchItem.expandActionView();
        searchText.setText(request, TextView.BufferType.EDITABLE);
        searchField.requestFocus();
    }

    private void hideSearchEditText() {
        searchField.setVisibility(View.GONE);
    }
}
