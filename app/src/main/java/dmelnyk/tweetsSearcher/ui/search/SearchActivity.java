package dmelnyk.tweetsSearcher.ui.search;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
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

    private final String FRAGMENT_TAG = this.getClass().getSimpleName() + "retain fragment";

    @Inject
    Contract.ISearchPresenter presenter;

    @BindView(R.id.search_field)
    EditText searchField;

    RetainFragment retainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        MyApp.get(this).getAppComponent().add(new SearchModule()).inject(this);

        presenter.bindView(this);

        instantiateRetainFragment();
        instantiateViews();
        instantiateToolbar();
    }

    @Override
    protected void onStop() {
        retainFragment.savePresenter(presenter);
        super.onStop();
    }

    private void instantiateToolbar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_edit_text_search);
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
        presenter.loadTweets(textChanged);
    }

    @Override
    public void onAnimateSearchView() {
//        int height = getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
        searchField.animate()
                .translationY(-600)
                .setDuration(2500)
                .start();
        Toast.makeText(this, "animation!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShopProgress() {

    }

    @Override
    public void onHideProgress() {

    }

    @Override
    public void onShowErrorToast(String message) {

    }
}
