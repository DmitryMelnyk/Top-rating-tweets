package dmelnyk.tweetsSearcher.application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dmelnyk.tweetsSearcher.utils.RxSchedulers;

/**
 * Created by dmitry on 29.04.17.
 */

@Module
public class RxModule {

    @Provides
    @Singleton
    RxSchedulers provideRxSchedulers() {
        return new RxSchedulers();
    }
}
