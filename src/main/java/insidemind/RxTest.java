package insidemind;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

import java.util.ArrayList;
import java.util.List;

public class RxTest {

    public static void main(String[] args) {

        Logger log = LogManager.getLogger(RxTest.class);

        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext("Hello, world!");
                        subscriber.onCompleted();
                    }
                }
        );

        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onCompleted() {
                System.out.println("Completed");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Ouch");
            }
        };

        myObservable.subscribe(mySubscriber);

        // Simpler code ####

        Observable.just("Hello world, second time").subscribe(System.out::println);

        // Transformations

        Observable.just("Hello world! - Marcin", "cześć").subscribe(System.out::println);
        Observable.just("Hello world!").subscribe(x -> System.out.println(x + " - Marcin"));

        Observable.just("Hello world!")
                .map(s -> s + ": Marcin : after map")
                .flatMap(s -> Observable.just("coś inneego"))
                .subscribe(System.out::println);

        // list
        System.out.println("#### LISTS");
        List<String> list = new ArrayList<>();
        list.add("Marcin");
        list.add("Ola");
        list.add("Tosia");
        list.add("Helenix");

        //Observable.from(list).subscribe(System.out::println);


        Subscription sub = Observable.just(list)
                .flatMap(Observable::from)
                .filter(s -> s.contains("a"))
                .take(2)
                .doOnNext(System.out::print)
                .subscribe(System.out::println);

        // Observable.from(list).subscribe(System.out::println);
        System.out.println();
        System.out.println();

        Subscription completed = Observable.just("Hello world!")
                .map(s -> s + ": Marcin : after map")
                .flatMap(Observable::just)
//                .map(String::length)
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        log.info("Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Ouch!");
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                    }
                });
    }


}
