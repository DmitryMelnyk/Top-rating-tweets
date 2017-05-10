package dmelnyk.tweetsSearcher.ui.search;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.tapadoo.alerter.Alert;
import com.tapadoo.alerter.Alerter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmelnyk.tweetsSearcher.R;
import dmelnyk.tweetsSearcher.application.MyApp;
import dmelnyk.tweetsSearcher.business.model.Tweet;
import dmelnyk.tweetsSearcher.ui.search.di.SearchModule;
import dmelnyk.tweetsSearcher.ui.search.utils.RetainFragment;
import dmelnyk.tweetsSearcher.ui.search.utils.TweetAdapter;
import dmelnyk.tweetsSearcher.ui.web.WebViewActivity;
import io.reactivex.Observable;

public class SearchActivity extends AppCompatActivity
        implements Contract.ISearchView, RefDialog.ReferenceListener {

    private final String TAG = this.getClass().getSimpleName();
    private final String FRAGMENT_TAG = TAG + "retain fragment";
    private final String STATE_EMPTY = TAG + "empty";
    private final String STATE_NON_EMPTY = TAG + "not empty";
    private static final int SPEECH_REQUEST_CODE = 0;

    public static final int TOAST_CODE_EMPTY = 0;
    public static final int TOAST_CODE_BAD_REQUEST = 1;
    public static final int TOAST_CODE_NO_INTERNET_CONNECTION = 2;

    @IntDef( {TOAST_CODE_EMPTY, TOAST_CODE_BAD_REQUEST, TOAST_CODE_NO_INTERNET_CONNECTION})
    @Retention(RetentionPolicy.CLASS)
    public @interface ErrorCode {}

    @Inject
    Contract.ISearchPresenter presenter;

    @BindView(R.id.twitterRecycler)
    RecyclerView twitterRecycler;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private EditText searchText;
    private RetainFragment retainFragment;

    private TweetAdapter adapter;
    // initial state
    private String state = STATE_EMPTY;
    private String searchRequest = "";
    private ArrayList<Tweet> tweets = new ArrayList<>();
    private SearchView searchView;

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

        setSupportActionBar(toolbar);
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
//        state = STATE_NON_EMPTY;
    }

    public void initializeNonEmptyState() {
        initializeRecyclerView();
        initializeSwipeRefreshLayout();
        // show data in RecyclerView
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        saveDataInFragment();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy(). unBind View");
        presenter.unbindView();
        super.onDestroy();
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

        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchText = (EditText) searchView.findViewById(
                android.support.v7.appcompat.R.id.search_src_text);

        searchText.setGravity(View.TEXT_ALIGNMENT_CENTER);

        menu.findItem(R.id.actionRecord)
                .setOnMenuItemClickListener(item -> {
                    expandSearchView();
                    displaySpeechRecognizer();
                    return false;
                });
        // observable can be created after initializing 'searchText'
        createObservable();
        return super.onCreateOptionsMenu(menu);
    }

    private void expandSearchView() {
        searchView.onActionViewExpanded();
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            Log.d(TAG, "spoken text = " + spokenText);
            searchRequest = spokenText;
            searchText.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                                        onShowErrorToast(3);
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
        state = STATE_NON_EMPTY;
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
    public void onShowErrorToast(@ErrorCode int code) {
        String message = "";
        switch (code) {
            case TOAST_CODE_BAD_REQUEST: // bad searchRequest
                message = getString(R.string.toast_bad_request);
                break;
            case TOAST_CODE_NO_INTERNET_CONNECTION: // no internet Connection;
                message = getString(R.string.toast_no_internet);
                break;
            case TOAST_CODE_EMPTY: // request field is empty;
                message = getString(R.string.toast_empty);
        }

        Alerter.create(this)
                .setText(message)
                .setBackgroundColor(R.color.colorPrimaryDark)
                .setDuration(4000)
                .show();
    }

    @Override
    public void setReference(String reference) {
        Intent webIntent = new Intent(this, WebViewActivity.class);
        webIntent.putExtra(WebViewActivity.KEY_URL, reference);
        startActivity(webIntent);
    }
}
