package com.ypz.rxjavademo;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;


import com.ypz.rxjavademo.base.ItemValue;

import java.util.concurrent.TimeUnit;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.Observable.create;
import static io.reactivex.Observable.empty;
import static io.reactivex.Observable.just;
import static io.reactivex.Observable.range;
import static io.reactivex.Observable.timer;

/**
 * Created by 易庞宙 on 2019 2019/3/14 10:26
 * email: 1986545332@qq.com
 */
public class TestJava {

    private ItemValue itemValue = null;

    public void test() {


        Consumer<Long> consumer1 = aLong -> {

        };

        Consumer<Long> consumer2 = aLong -> {

        };

        Function<Integer, Observable<Integer>> function = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                return Observable.range(integer * 10, 2);
            }
        };
        BiFunction<Integer, Integer, Observable<Integer>> biFunction = (initValue, changeValue) -> Observable.just(initValue + changeValue);
        just(1, 2, 3).flatMap(function, biFunction).subscribe();

        range(1, 10).groupBy(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return null;
            }
        }).subscribe(stringIntegerGroupedObservable -> {

        });

        create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onComplete();
            }
        }).doOnComplete(() -> {

            //   Log.d(TAG, "触发重订阅");
        }).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            private int n = 0;

            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        if (n != 3) {
                            n++;
                            return timer(3, TimeUnit.SECONDS);
                        } else {
                            return empty();
                        }
                    }
                });
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String s) {
                // Log.d(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                // Log.d(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                //  Log.d(TAG, "onComplete: ");
            }
        });


        Observable<Long> observable2 = Observable.interval(150, TimeUnit.MILLISECONDS)
                .take(4)
                .subscribeOn(Schedulers.newThread());
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(3)
                .subscribeOn(Schedulers.newThread())
                .withLatestFrom(observable2, (aLong, aLong2) -> {
                    System.out.print("aLong:" + aLong + "\t aLong2:" + aLong2 + "\t");
                    return aLong + aLong2;
                })
                .subscribe(o -> System.out.println("===>" + o + "\t"));


    }

    public Drawable sub(Resources resources, int resourcesId) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, BitmapFactory.decodeResource(resources, resourcesId));
        roundedBitmapDrawable.setCornerRadius(30);
        return roundedBitmapDrawable;
    }

    public ItemValue getItemValue() {
        if (itemValue==null) return new ItemValue("",1L);
        return itemValue;
    }
}
