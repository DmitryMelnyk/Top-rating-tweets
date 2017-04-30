package dmelnyk.tweetsSearcher.application;

import javax.inject.Singleton;

import dagger.Component;
import dmelnyk.tweetsSearcher.ui.search.dagger.SearchComponent;
import dmelnyk.tweetsSearcher.ui.search.dagger.SearchModule;

/**
 * Created by dmitry on 29.04.17.
 */

@Singleton
@Component(modules = {
        AppModule.class,
        RxModule.class })
public interface AppComponent {

    SearchComponent add(SearchModule module);
}
