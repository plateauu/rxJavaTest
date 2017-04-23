package weather;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;

public class WeatherClient {

    private static final Logger log = LogManager.getLogger(WeatherClient.class);


    public Weather fetch(String city) throws InterruptedException {
        log.info("Loading for {}", city);
        Thread.sleep(900);
        //http
        return new Weather();
    }

    public Observable<Weather> rxFetch(String city) throws InterruptedException {
        return Observable.fromCallable(
                () ->
                        fetch(city));

    }
}
