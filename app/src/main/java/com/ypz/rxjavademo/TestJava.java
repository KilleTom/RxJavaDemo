package com.ypz.rxjavademo;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

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


        Observable.create(new ObservableOnSubscribe<String>() {
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
                            return Observable.timer(3, TimeUnit.SECONDS);
                        } else {
                            return Observable.empty();
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


    }

    public Drawable sub(Resources resources,int resourcesId){
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, BitmapFactory.decodeResource(resources,resourcesId));
        roundedBitmapDrawable.setCornerRadius(30);
        return roundedBitmapDrawable;
    }
}
