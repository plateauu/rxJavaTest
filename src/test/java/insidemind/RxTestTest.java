package insidemind;

import cache.CacheServer;
import dao.Person;
import dao.PersonDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import weather.Weather;
import weather.WeatherClient;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RxTestTest {

    private static final BigDecimal FALLBACK2 = BigDecimal.ONE.negate();
    Logger log = LogManager.getLogger();

    @Test
    public void test1() {
        Observable<Integer> numbers = Observable.just(1, 2, 3);
        Observable<String> strings = Observable.just("A", "B", "C", "D");

        numbers.zipWith(strings, (Number n, String s) -> n + s)
                .repeat(5)
                .toBlocking()
                .subscribe(this::print);
    }

    void print(Object object) {
        log.info("Got: {}", object);
    }

    @Test
    public void env() {
        System.out.println(System.getenv());
        System.out.println(System.getProperties());
    }

    @Test
    public void showLambda() {
        System.out.println();
    }

    @Test
    public void testMethod1() {
        final Observable<String> obs = Observable.just("42");
        obs.subscribe(this::print);
    }

    @Test
    public void testMethod2() {
        final Observable<String> obs = Observable.just("42", "43", "44");
        obs.subscribe(this::print);

    }


    WeatherClient client = new WeatherClient();

    @Test
    public void testMethod3() throws Exception {
        print(client.fetch("warsaw"));
    }

    @Test
    public void testMethod4() throws Exception {
        final Observable<Weather> warsaw = client.rxFetch("warsaw");
        warsaw.subscribe((Weather w) -> this.print(w));
    }

    @Test
    public void testMethod5() throws Exception {
        final Observable<Weather> warsaw = client.rxFetch("warsaw");
        final Observable<Weather> timeout = warsaw
                .timeout(1, TimeUnit.SECONDS);
        timeout.subscribe(this::print);

    }

    @Test
    public void testMethod6() throws Exception {
        final Observable<Weather> warsaw = client.rxFetch("warsaw");
        final Observable<Weather> timeout = warsaw
                .timeout(800, TimeUnit.MILLISECONDS);
        timeout.subscribe(this::print);
    }

    @Test
    public void testMethod21() throws Exception {
        Observable<Weather> weather1 = client.rxFetch("Warsaw");
        Observable<Weather> weather2 = client.rxFetch("Radom");

        final Observable<Weather> pogody = weather1.mergeWith(weather2);
        //zwróci 2 obiekty
        pogody.subscribe(this::print);
    }

    @Test
    public void testMethod22() throws Exception {
        Observable<Weather> weather1 = client.rxFetch("Warsaw");
        Observable<Weather> weather2 = client.rxFetch("Radom");

        weather1.subscribe(this::print);
        //900ms później

        final Observable<Weather> pogody = weather1.mergeWith(weather2);
        //zwróci 2 obiekty
        pogody.subscribe(this::print);
    }

    private final PersonDao dao = new PersonDao();

    @Test
    public void testMethod223() throws Exception {
        final Observable<Weather> łódź = client
                .rxFetch("Łódź")
                .subscribeOn(Schedulers.io()); //nie używaj io()

        final Observable<Person> person = dao
                .rxFindById(42)
                .subscribeOn(Schedulers.io());

        Observable<String> str =
                łódź.zipWith(person, (Weather w, Person p) -> w + " : " + p);

        str.subscribe(this::print);

        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void testMethod2213() throws Exception {
        final Observable<String> strings = Observable.just("A", "B", "C");
        final Observable<Integer> numbers = Observable.range(1, 3);

        final Observable<String> s2 = Observable.zip(
                strings,
                numbers,
                (s, n) -> s + n
        );

        s2.subscribe(this::print);
    }

    @Test
    public void testMethod22134() throws Exception {
        final Observable<String> strings = Observable
                .just("A", "B", "C")
                .repeat();
        final Observable<Integer> numbers = Observable
                .range(1, 10)
                .map(x -> x * 10);

        final Observable<String> s2 = Observable.zip(
                strings,
                numbers,
                (s, n) -> s + n
        );

        s2.subscribe(this::print);
    }

    @Test
    public void testMethod12() throws Exception {
        Schedulers.io();
        Schedulers.test();
        Schedulers.computation();
        Schedulers.from(Executors.newFixedThreadPool(10));
        new ThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    @Test
    public void testMethod() throws Exception {
        CacheServer eu = new CacheServer();
        CacheServer us = new CacheServer();

        Observable<String> reu = eu.rxFindBy(42);
        Observable<String> rus = us.rxFindBy(42);

        Observable
                .merge(reu.timeout(1, TimeUnit.SECONDS), rus)
                .first()
                .subscribe(this::print)
        ;
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void testMethod231() throws Exception {
        final Observable<Object> empty = Observable.empty();
        empty.subscribe(this::print);
    }

    @Test
    public void testMethod235() throws Exception {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .map(x -> x * Math.PI)
                .subscribe(this::print);
        TimeUnit.SECONDS.sleep(7);
    }

    File dir = new File("/home/plateauu/tmp/wjug");

    @Test
    public void testMethod13() throws Exception {
        childrenOf(dir)
                .subscribe(this::print);

    }

    @Test
    public void testMethod32() throws Exception {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .map(x -> childrenOf2(dir))
                .toBlocking()
                .subscribe(this::print);
    }

    @Test
    public void testMethod111() throws Exception {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .flatMap(x -> childrenOf(dir))
                .distinct() // <- wyciek pamieci | DistinctUntilChange()
                .toBlocking()
                .subscribe(this::print);
    }


    private List<String> childrenOf2(File dir) {
        return childrenOf(dir)
                .toList()            // [a.txt, b.txt, c.txt]
                .toBlocking()       //blokuje wątek klienta
                .single();          //wywala się gdy strumień nie ma dokładnie jednego elementu
    }

    private Observable<String> childrenOf(File dir) {
        final File[] files = dir.listFiles();
        return Observable
                .from(files)
                .map(File::getName);
    }

    @Test
    public void testMethod213() throws Exception {
        Observable
                .empty()
                .single()
                .subscribe();
    }

    @Test
    public void testMethod214() throws Exception {
        Observable
                .just(1, 2)
                .single()
                .subscribe();
    }

    @Test
    public void testMethod215() throws Exception {
        Observable
                .interval(100, TimeUnit.MILLISECONDS)
                .single()
                .subscribe();

        TimeUnit.SECONDS.sleep(1);
//        TimeUnit.MILLISECONDS.sleep(101);
    }


    @Test
    public void testMethod132() throws Exception {
        final Observable<BigDecimal> response = verySlowSoapService()
                .timeout(1, TimeUnit.SECONDS)
                .doOnError(ex -> log.warn("Ooopse " + ex))//nigdy tak nie robimy
//                .retry() // w nieskończoność
                .retry(4)
//                .retryWhen()
                .onErrorReturn(x -> BigDecimal.ONE.negate());

        response
                .toBlocking()
                .subscribe(this::print);
    }

    @Test
    public void testMethod133() throws Exception {
        final TestScheduler testScheduler = Schedulers.test();

        final Observable<BigDecimal> response = verySlowSoapService()
                .timeout(1, TimeUnit.SECONDS, testScheduler)
                .doOnError(ex -> log.warn("Ooopse " + ex))//nigdy tak nie robimy
                .retry(4)
                .onErrorReturn(x -> FALLBACK2);

        //Awaitility
        //java.time.Clock

        final TestSubscriber<BigDecimal> subscriber = new TestSubscriber<>();
        response.subscribe(subscriber);

        subscriber.assertNoErrors();
        subscriber.assertNoValues();

        testScheduler.advanceTimeBy(4_999, TimeUnit.MILLISECONDS);
        subscriber.assertNoErrors();
        subscriber.assertNoValues();

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        subscriber.assertNoErrors();
        subscriber.assertValue(FALLBACK2);
    }

    private Observable<BigDecimal> verySlowSoapService() {
        return Observable
                .timer(1, TimeUnit.MINUTES)
                .map(x -> BigDecimal.ZERO);
    }

}
