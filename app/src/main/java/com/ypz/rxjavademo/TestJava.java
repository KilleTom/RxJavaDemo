package com.ypz.rxjavademo;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 易庞宙 on 2019 2019/3/14 10:26
 * email: 1986545332@qq.com
 */
public class TestJava {

    public void test() {


        Consumer<Long> consumer1 = aLong -> {

        };

        Consumer<Long> consumer2 = aLong -> {

        };


        ConnectableObservable<Long> connectableObservable = Observable.create(
                (ObservableEmitter<Long> emitter) ->
                        Observable.interval(10, TimeUnit.MILLISECONDS,Schedulers.computation()).take(Integer.MAX_VALUE).subscribe(emitter::onNext)
        ).observeOn(Schedulers.newThread()).publish();

    }
}
