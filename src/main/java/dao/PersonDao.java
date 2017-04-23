package dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;

/**
 * Created by plateauu on 4/22/17.
 */
public class PersonDao {

    private static Logger log = LogManager.getLogger(PersonDao.class);

    public Person findById(int id) throws InterruptedException {
        //SQL
        log.info("Loading {}", id);
        Thread.sleep(900);
        return new Person();
    }

    public Observable<Person> rxFindById(int id) {
        return Observable.fromCallable(() ->
            findById(id)
        );
    }


}
