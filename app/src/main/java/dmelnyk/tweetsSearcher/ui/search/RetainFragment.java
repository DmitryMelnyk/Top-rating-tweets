package dmelnyk.tweetsSearcher.ui.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by dmitry on 29.04.17.
 */

public class RetainFragment extends Fragment {

    private Contract.ISearchPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    public Contract.ISearchPresenter restorePresenter() {
        return presenter;
    }

    public void savePresenter(Contract.ISearchPresenter presenter) {
        this.presenter = presenter;
    }
}
