package com.ypz.rxjavademo.operators

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ypz.rxjavademo.base.logIMessage
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class OperatorsConnectActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    private fun coldTobeHot() {
        /**
         * 使用publish操作转为hot（ConnectableObservable）保证事件发送按订阅先后统一发送解决并发问题
         * 只有使用了connect操作符才后真正的发送数据
         * 用这种方式保证线程安全发送数据
         * */
        val observable = Observable.create(ObservableOnSubscribe<Long> { emitter ->
            Observable.interval(10L, TimeUnit.MILLISECONDS, Schedulers.computation())
                    .take(Long.MAX_VALUE)
                    .subscribe {
                        if (it <= 10) emitter.onNext(it)
                        else emitter.onComplete()
                    }
        }).observeOn(Schedulers.newThread()).publish()
        observable.connect()
        observable.subscribe({ Log.i("coldObservable", "subscribe-1value:$it") })
        observable.subscribe { Log.i("coldObservable", "subscribe-2value:$it") }
    }


    private fun hotTobeCold() {
        Thread.sleep(500L)
        val consumer1 = Consumer<Long> { logIMessage("hotTobeCold-consumer1", "value$it") }
        val consumer2 = Consumer<Long> { logIMessage("hotTobeCold-consumer2", "value$it") }
        val connectableObservable = Observable.create { emitter: ObservableEmitter<Long> ->
            Observable.interval(10, TimeUnit.MILLISECONDS, Schedulers.computation()).take(Integer.MAX_VALUE.toLong())
                    .subscribe {
                        emitter.onNext(it)
                    }
        }.observeOn(Schedulers.newThread()).publish()
        connectableObservable.connect()
        val observable = connectableObservable.refCount()
        var dispose1 = observable.subscribe(consumer1)
        var dispose2 = observable.subscribe(consumer2)
        Thread.sleep(50L)
        dispose1.dispose()
        dispose2.dispose()
        logIMessage("hotTobeCold", "resetDataSource")
        dispose1 = observable.subscribe(consumer1)
        dispose2 = observable.subscribe(consumer2)
        Thread.sleep(50L)
        dispose1.dispose()
        dispose2.dispose()
    }
}
