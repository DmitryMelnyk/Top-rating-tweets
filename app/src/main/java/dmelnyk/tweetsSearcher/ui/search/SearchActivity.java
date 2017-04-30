package dmelnyk.tweetsSearcher.ui.search;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.widget.RxTextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmelnyk.tweetsSearcher.R;
import dmelnyk.tweetsSearcher.application.MyApp;
import dmelnyk.tweetsSearcher.ui.search.dagger.SearchModule;
import io.reactivex.Observable;

public class SearchActivity extends AppCompatActivity implements Contract.ISearchView {

    private final int ANIM_DURATION = 5000; // duration time of animation in milliseconds
    private final String FRAGMENT_TAG = this.getClass().getSimpleName() + "retain fragment";

    @Inject
    Contract.ISearchPresenter presenter;

    @BindView(R.id.search_field)
    EditText searchField;

    RetainFragment retainFragment;
    private EditText searchText;
    private SearchView searchView;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        MyApp.get(this).getAppComponent().add(new SearchModule()).inject(this);

        presenter.bindView(this);

        instantiateRetainFragment();
        instantiateViews();
    }

    @Override
    protected void onStop() {
        retainFragment.savePresenter(presenter);
        presenter.unbindView();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        searchItem = menu.findItem(R.id.actionSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchText = (EditText) searchView.findViewById(
                android.support.v7.appcompat.R.id.search_src_text);

        // creating Observable from SearchView's text
        Observable<CharSequence> observable = RxTextView.textChanges(searchText);
        presenter.loadTweets(observable);

        return super.onCreateOptionsMenu(menu);
    }

    private void instantiateRetainFragment() {
        retainFragment = (RetainFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (retainFragment == null) {
            retainFragment = new RetainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(retainFragment, FRAGMENT_TAG)
                    .commit();
        } else {
            presenter = retainFragment.restorePresenter();
        }
    }

    private void instantiatePresenter() {
    }

    private void instantiateViews() {
        Observable<CharSequence> textChanged = RxTextView.textChanges(searchField);
        presenter.forwardInputData(textChanged);
    }

    @Override
    public void onAnimateSearchView() {
        searchField.animate()
                .alpha(0) // disappearing
                .setDuration(ANIM_DURATION)
                .start();
    }

    private void hideSearchField() {
        // hide searchField after animation
        searchField.setVisibility(View.GONE);
    }

    @Override
    public void onShowProgress() {
        // TODO: it's mock
        runOnUiThread(
                () -> Toast.makeText(this, "Start Progress", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onHideProgress() {

    }

    @Override
    public void onShowErrorToast(String message) {

    }

    @Override
    public void onChangeInputTextField(CharSequence request) {
        searchItem.expandActionView();
        searchText.setText(request, TextView.BufferType.EDITABLE);
        searchField.requestFocus();
    }
}
