package cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Created by plateauu on 4/22/17.
 */
public class CacheServer {

    private static final Logger log = LogManager.getLogger();

    public String findBy(long key) throws InterruptedException {
        log.info("Loading from Memcached: {}", key);
        TimeUnit.MILLISECONDS.sleep(900);
        return "<data>" + key + "<data>";
    }

    public Observable<String> rxFindBy(long key) {
        return Observable
                .fromCallable(() -> findBy(key))
                .subscribeOn(Schedulers.io());

    }

}
